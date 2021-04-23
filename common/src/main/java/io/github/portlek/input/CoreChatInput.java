/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
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
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class created to get inputs from players.
 *
 * @param <T> the input type.
 * @param <P> the input sender type.
 * @param <X> the task type.
 * @param <L> the listener type.
 */
public abstract class CoreChatInput<T, P, X, L> implements ChatInput {

  /**
   * the cancel.
   */
  @NotNull
  private final String cancel;

  /**
   * the expire.
   */
  private final long expire;

  /**
   * the invalid input message.
   */
  @Nullable
  private final String invalidInputMessage;

  /**
   * the is valid input.
   */
  @NotNull
  private final BiPredicate<Sender<P>, String> isValidInput;

  /**
   * the on cancel.
   */
  @NotNull
  private final Consumer<Sender<P>> onCancel;

  /**
   * the on expire.
   */
  @NotNull
  private final Consumer<Sender<P>> onExpire;

  /**
   * the on finish.
   */
  @NotNull
  private final BiConsumer<Sender<P>, T> onFinish;

  /**
   * the on invalid input.
   */
  @NotNull
  private final BiPredicate<Sender<P>, String> onInvalidInput;

  /**
   * the plugin.
   */
  @NotNull
  private final ChatInputPlugin<X, L> plugin;

  /**
   * the repeat.
   */
  private final boolean repeat;

  /**
   * the send value message.
   */
  @Nullable
  private final String sendValueMessage;

  /**
   * the sender.
   */
  @NotNull
  private final Sender<P> sender;

  /**
   * the set value.
   */
  @NotNull
  private final BiFunction<Sender<P>, String, T> setValue;

  /**
   * the expire task.
   */
  @Nullable
  private Task expireTask;

  /**
   * ctor.
   *
   * @param plugin the plugin.
   * @param sender the sender.
   * @param invalidInputMessage the invalid input message.
   * @param sendValueMessage the send value message.
   * @param isValidInput the is valid input.
   * @param setValue the set value.
   * @param onFinish the on finish.
   * @param onCancel the on cancel.
   * @param onExpire the on expire.
   * @param cancel the cancel.
   * @param onInvalidInput the on invalid input.
   * @param repeat the repeat.
   * @param expire the expire.
   */
  protected CoreChatInput(@NotNull final ChatInputPlugin<X, L> plugin, @NotNull final Sender<P> sender,
                          @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                          @NotNull final BiPredicate<Sender<P>, String> isValidInput,
                          @NotNull final BiFunction<Sender<P>, String, T> setValue,
                          @NotNull final BiConsumer<Sender<P>, T> onFinish, @NotNull final Consumer<Sender<P>> onCancel,
                          @NotNull final Consumer<Sender<P>> onExpire, @NotNull final String cancel,
                          @NotNull final BiPredicate<Sender<P>, String> onInvalidInput, final boolean repeat,
                          final long expire) {
    this.plugin = plugin;
    this.sender = sender;
    this.invalidInputMessage = invalidInputMessage;
    this.sendValueMessage = sendValueMessage;
    this.isValidInput = isValidInput;
    this.setValue = setValue;
    this.onFinish = onFinish;
    this.onCancel = onCancel;
    this.onExpire = onExpire;
    this.cancel = cancel;
    this.onInvalidInput = onInvalidInput;
    this.repeat = repeat;
    this.expire = expire;
  }

  @Override
  public final void start() {
    this.plugin.registerEvent(this.getListener());
    if (this.expire != -1L) {
      this.expireTask = this.createTask(
        this.plugin.createRunTaskLater(() ->
          Optional.ofNullable(this.expireTask)
            .filter(task -> !task.isCancelled())
            .ifPresent(task -> {
              this.onExpire.accept(this.sender);
              this.unregister();
            }), this.expire));
    }
    Optional.ofNullable(this.sendValueMessage)
      .ifPresent(this.sender::sendMessage);
  }

  /**
   * runs when the sender push an input.
   *
   * @param event the event to apply as a send message event.
   */
  protected final void onChat(@NotNull final ChatEvent<P> event) {
    if (!this.sender.getUniqueId().equals(event.getSender().getUniqueId())) {
      return;
    }
    event.cancel();
    final String message = event.getMessage();
    if (message.equalsIgnoreCase(this.cancel)) {
      this.onCancel.accept(this.sender);
      this.unregister();
      return;
    }
    if (this.isValidInput.test(this.sender, message)) {
      final T value = this.setValue.apply(this.sender, message);
      this.onFinish.accept(this.sender, value);
      this.unregister();
    } else {
      if (this.onInvalidInput.test(this.sender, message)) {
        Optional.ofNullable(this.invalidInputMessage)
          .ifPresent(this.sender::sendMessage);
        Optional.ofNullable(this.sendValueMessage)
          .filter(s -> this.repeat)
          .ifPresent(this.sender::sendMessage);
      }
      if (!this.repeat) {
        this.unregister();
      }
    }
  }

  /**
   * runs when the send quits from the game.
   *
   * @param event the event to apply as a quit event.
   */
  protected final void onQuit(@NotNull final QuitEvent<P> event) {
    if (event.getSender().getUniqueId().equals(this.sender.getUniqueId())) {
      this.onCancel.accept(this.sender);
      this.unregister();
    }
  }

  /**
   * creates a {@link Task} from the given object.
   *
   * @param object the object to create.
   *
   * @return a {@link Task} instance.
   */
  @NotNull
  protected abstract Task createTask(@NotNull X object);

  /**
   * obtains the listener to register.
   *
   * @return the listener to regsiter.
   */
  protected abstract L getListener();

  /**
   * un register all listeners.
   */
  protected abstract void unregisterListeners();

  /**
   * unregisters the registered listeners and cancels all tasks.
   */
  private void unregister() {
    this.unregisterListeners();
    Optional.ofNullable(this.expireTask).ifPresent(Task::cancel);
  }
}
