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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class created to get inputs from players.
 *
 * @param <T> the input type.
 * @param <P> the input sender type.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CoreChatInput<T, P> implements ChatInput {

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

  @Override
  public final void start() {
    this.registerEvent();
    if (this.expire != -1L) {
      this.expireTask = this.createRunTaskLater(() ->
        Optional.ofNullable(this.expireTask)
          .filter(task -> !task.isCancelled())
          .ifPresent(task -> {
            this.onExpire.accept(this.sender);
            this.unregister();
          }), this.expire);
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
   * unregisters the registered listeners and cancels all tasks.
   */
  private void unregister() {
    this.unregisterListeners();
    Optional.ofNullable(this.expireTask).ifPresent(Task::cancel);
  }
}
