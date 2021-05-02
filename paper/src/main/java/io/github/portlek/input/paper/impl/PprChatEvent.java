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

package io.github.portlek.input.paper.impl;

import io.github.portlek.input.Sender;
import io.github.portlek.input.event.ChatEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link ChatEvent}.
 */
public final class PprChatEvent implements ChatEvent<Player> {

  /**
   * the event.
   */
  @NotNull
  private final AsyncChatEvent event;

  /**
   * the sender.
   */
  @NotNull
  @Getter
  private final Sender<Player> sender;

  /**
   * ctor.
   *
   * @param event the event.
   */
  public PprChatEvent(@NotNull final AsyncChatEvent event) {
    this.event = event;
    this.sender = new PprSender(this.event.getPlayer());
  }

  @Override
  public void cancel() {
    this.event.setCancelled(true);
  }

  @NotNull
  @Override
  public String getMessage() {
    return ((TextComponent) this.event.message()).content();
  }
}
