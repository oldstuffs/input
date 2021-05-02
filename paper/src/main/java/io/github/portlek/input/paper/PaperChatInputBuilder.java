/*
 * MIT License
 *
 * Copyright (c) 2021 MrNemo64
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.portlek.input.paper;

import io.github.portlek.input.ChatInputBuilder;
import io.github.portlek.input.CoreChatInput;
import io.github.portlek.input.paper.impl.PprSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * builder for the {@link CoreChatInput} class.
 *
 * @param <T> the {@link CoreChatInput} type.
 */
public final class PaperChatInputBuilder<T> extends ChatInputBuilder<T, Player> {

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * ctor.
   *
   * @param plugin The main class of the plugin
   * @param player The player that will send the input
   */
  private PaperChatInputBuilder(@NotNull final Plugin plugin, @NotNull final Player player) {
    super(new PprSender(player));
    this.plugin = plugin;
  }

  /**
   * initiates a {@code this} instance.
   *
   * @param plugin the plugin to initiate.
   * @param player the player to initiate.
   * @param <T> the value type.
   *
   * @return an instance of {@code this}.
   */
  @NotNull
  public static <T> PaperChatInputBuilder<T> builder(@NotNull final Plugin plugin, @NotNull final Player player) {
    return new PaperChatInputBuilder<>(plugin, player);
  }

  @NotNull
  @Override
  public PaperChatInput<T> build() {
    return new PaperChatInput<>(this.plugin, this.sender, this.invalidInputMessage, this.sendValueMessage,
      this.isValidInput, this.setValue, this.onFinish, this.onCancel, this.onExpire, this.cancel, this.onInvalidInput,
      this.repeat, this.expire);
  }
}
