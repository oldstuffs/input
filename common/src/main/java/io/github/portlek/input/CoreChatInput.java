/*
 * MIT License
 *
 * Copyright (c) 2020 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.portlek.input;

import io.github.portlek.input.event.ChatEvent;
import io.github.portlek.input.event.QuitEvent;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class created to get inputs from players.
 *
 * @param <T> The input type. Ex: String, Integer, Boolean
 * @author Nemo_64
 * @version 1.1
 */
@RequiredArgsConstructor
public abstract class CoreChatInput<T, P, S extends Sender<P>, X, A extends ChatEvent<P>, B extends QuitEvent<P>, L>
    implements ChatInput<T, X, L> {

    @NotNull
    private final ChatInputPlugin<X, L> plugin;

    @NotNull
    private final S sender;

    @Nullable
    private final String invalidInputMessage;

    @Nullable
    private final String sendValueMessage;

    @NotNull
    private final BiFunction<S, String, Boolean> isValidInput;

    @NotNull
    private final BiFunction<S, String, T> setValue;

    @NotNull
    private final BiConsumer<S, T> onFinish;

    @NotNull
    private final Consumer<S> onCancel;

    @NotNull
    private final Consumer<S> onExpire;

    @NotNull
    private final String cancel;

    @NotNull
    private final BiFunction<S, String, Boolean> onInvalidInput;

    private final boolean repeat;

    private final long expire;

    @Nullable
    private T value;

    @Nullable
    private Task<X> expireTask;

    /**
     * Gets the value that the player has inputted or the default value
     *
     * @return The value
     */
    @NotNull
    @Override
    public final Optional<T> getValue() {
        return Optional.ofNullable(this.value);
    }

    /**
     * When this method is called the input will be asked to the player
     */
    @Override
    public final void start() {
        this.plugin.registerEvent(this.self());
        if (this.expire != -1L) {
            this.expireTask = this.createTask(
                this.plugin.createRunTaskLater(() ->
                    this.getExpireTask()
                        .filter(task -> !task.isCancelled())
                        .ifPresent(task -> {
                            this.onExpire.accept(this.sender);
                            this.unregister();
                        }), this.expire));
        }
        this.getSendValueMessage()
            .ifPresent(this.sender::sendMessage);
    }

    /**
     * When this method is called all the events in this input handler are
     * unregistered<br>
     * Only use if necessary. The class unregisters itself when it has finished/the
     * player leaves
     */
    @Override
    public final void unregister() {
        this.unregisterListeners();
        this.getExpireTask().ifPresent(Task::cancel);
    }

    @NotNull
    @Override
    public final Optional<Task<X>> getExpireTask() {
        return Optional.ofNullable(this.expireTask);
    }

    @NotNull
    @Override
    public final Optional<String> getInvalidInputMessage() {
        return Optional.ofNullable(this.invalidInputMessage);
    }

    @NotNull
    @Override
    public final Optional<String> getSendValueMessage() {
        return Optional.ofNullable(this.sendValueMessage);
    }

    @NotNull
    public abstract Task<X> createTask(@NotNull X task);

    public final void onChat(@NotNull final A event) {
        if (!this.sender.getUniqueId().equals(event.sender().getUniqueId())) {
            return;
        }
        event.cancel();
        final String message = event.message();
        if (message.equalsIgnoreCase(this.cancel)) {
            this.onCancel.accept(this.sender);
            this.unregister();
            return;
        }
        if (this.isValidInput.apply(this.sender, message)) {
            this.value = this.setValue.apply(this.sender, message);
            this.onFinish.accept(this.sender, this.value);
            this.unregister();
        } else {
            if (this.onInvalidInput.apply(this.sender, message)) {
                this.getInvalidInputMessage()
                    .ifPresent(this.sender::sendMessage);
                this.getSendValueMessage()
                    .filter(s -> this.repeat)
                    .ifPresent(this.sender::sendMessage);
            }
            if (!this.repeat) {
                this.unregister();
            }
        }
    }

    public final void onQuit(@NotNull final B event) {
        if (event.sender().getUniqueId().equals(this.sender.getUniqueId())) {
            this.onCancel.accept(this.sender);
            this.unregister();
        }
    }

}
