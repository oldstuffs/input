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

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a builder class to create {@link ChatInput} instance.
 *
 * @param <T> the value type.
 * @param <P> the input sender type.
 * @param <X> the task type.
 * @param <L> the listener type.
 */
public abstract class ChatInputBuilder<T, P, X, L> {

  /**
   * the plugin.
   */
  @NotNull
  protected final ChatInputPlugin<X, L> plugin;

  /**
   * the sender.
   */
  @NotNull
  protected final Sender<P> sender;

  /**
   * the on invalid input.
   */
  @NotNull
  protected BiPredicate<Sender<P>, String> onInvalidInput = (p, mes) -> true;

  /**
   * the is valid input.
   */
  @NotNull
  protected BiPredicate<Sender<P>, String> isValidInput = (p, mes) -> true;

  /**
   * the set value.
   */
  @NotNull
  protected BiFunction<Sender<P>, String, T> setValue = (p, mes) -> this.value;

  /**
   * the on finish.
   */
  @NotNull
  protected BiConsumer<Sender<P>, T> onFinish = (p, val) -> {
  };

  /**
   * the on cancel.
   */
  @NotNull
  protected Consumer<Sender<P>> onCancel = p -> {
  };

  /**
   * the on expire.
   */
  @NotNull
  protected Consumer<Sender<P>> onExpire = p -> {
  };

  /**
   * the invalid input message.
   */
  @Nullable
  protected String invalidInputMessage = "That is not a valid input";

  /**
   * the send value message.
   */
  @Nullable
  protected String sendValueMessage = "Send in the chat the value";

  /**
   * the expire.
   */
  protected long expire = -1L;

  /**
   * the repeat.
   */
  protected boolean repeat = true;

  /**
   * the cancel.
   */
  @NotNull
  protected String cancel = "cancel";

  /**
   * the value.
   */
  @Nullable
  private T value;

  /**
   * ctor.
   *
   * @param plugin the plugin.
   * @param sender the sender.
   */
  protected ChatInputBuilder(@NotNull final ChatInputPlugin<X, L> plugin, @NotNull final Sender<P> sender) {
    this.plugin = plugin;
    this.sender = sender;
  }

  /**
   * sets {@link ChatInputBuilder#expire} and return {@code this}.
   *
   * @param expire the expire to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> expire(final long expire) {
    this.expire = expire;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#invalidInputMessage} and return {@code this}.
   *
   * @param invalidInputMessage the invalid input message to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> invalidInputMessage(@Nullable final String invalidInputMessage) {
    this.invalidInputMessage = invalidInputMessage;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#sendValueMessage} and return {@code this}.
   *
   * @param sendValueMessage the send value message to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> sendValueMessage(@Nullable final String sendValueMessage) {
    this.sendValueMessage = sendValueMessage;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#cancel} and return {@code this}.
   *
   * @param cancel the cancel to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> toCancel(@NotNull final String cancel) {
    this.cancel = cancel;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#value} and return {@code this}.
   *
   * @param value the value to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> defaultValue(@Nullable final T value) {
    this.value = value;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#repeat} and return {@code this}.
   *
   * @param repeat the repeat to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> repeat(final boolean repeat) {
    this.repeat = repeat;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#onExpire} and return {@code this}.
   *
   * @param onExpire the on expire to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> onExpire(@NotNull final Consumer<Sender<P>> onExpire) {
    this.onExpire = onExpire;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#onInvalidInput} and return {@code this}.
   *
   * @param onInvalidInput the on invalid input to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> onInvalidInput(
    @NotNull final BiPredicate<Sender<P>, String> onInvalidInput) {
    this.onInvalidInput = onInvalidInput;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#isValidInput} and return {@code this}.
   *
   * @param isValidInput the is valid input to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> isValidInput(@NotNull final BiPredicate<Sender<P>, String> isValidInput) {
    this.isValidInput = isValidInput;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#onFinish} and return {@code this}.
   *
   * @param onFinish the on finish to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> onFinish(@NotNull final BiConsumer<Sender<P>, T> onFinish) {
    this.onFinish = onFinish;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#onCancel} and return {@code this}.
   *
   * @param onCancel the on cancel to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> onCancel(@NotNull final Consumer<Sender<P>> onCancel) {
    this.onCancel = onCancel;
    return this;
  }

  /**
   * sets {@link ChatInputBuilder#setValue} and return {@code this}.
   *
   * @param setValue the set value to set.
   *
   * @return {@code this}.
   */
  @NotNull
  public final ChatInputBuilder<T, P, X, L> setValue(@NotNull final BiFunction<Sender<P>, String, T> setValue) {
    this.setValue = setValue;
    return this;
  }

  /**
   * builds the {@link ChatInput} instance.
   *
   * @return a {@link ChatInput} instance.
   */
  @NotNull
  public abstract ChatInput build();
}
