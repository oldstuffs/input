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
 *
 */

package io.github.portlek.input.paper;

import io.github.portlek.input.ChatInput;
import io.github.portlek.input.ChatPlatform;
import io.github.portlek.input.ChatTask;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation of {@link ChatPlatform}.
 */
@RequiredArgsConstructor
public final class PaperChatPlatform implements ChatPlatform<Player>, Listener {

  /**
   * the input.
   */
  @NotNull
  private final AtomicReference<ChatInput<?, Player>> input = new AtomicReference<>();

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * creates a new builder instance.
   *
   * @param platform the platform to create.
   * @param sender the sender to create.
   * @param <T> type of the value.
   *
   * @return a newly created builder instance.
   */
  @NotNull
  public static <T> ChatInput.Builder<T, Player> builder(@NotNull final PaperChatPlatform platform,
                                                         @NotNull final Player sender) {
    return ChatInput.builder(platform, new PprChatSender(sender));
  }

  @NotNull
  @Override
  public ChatTask createRunTaskLater(@NotNull final Runnable runnable, final long time) {
    return new PprChatTask(Bukkit.getScheduler().runTaskLater(this.plugin, runnable, time));
  }

  @Override
  public void init(@NotNull final ChatInput<?, Player> input) {
    this.input.set(input);
    Bukkit.getPluginManager().registerEvents(this, this.plugin);
  }

  @Override
  public void unregisterListeners() {
    HandlerList.unregisterAll(this);
  }

  /**
   * runs when the player sends a chat message.
   *
   * @param event the event to handle.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void whenChat(@NotNull final AsyncChatEvent event) {
    this.input.get().onChat(new PprChatEvent(event, new PprChatSender(event.getPlayer())));
  }

  /**
   * runs when the player quits the game.
   *
   * @param event the event to handle.
   */
  @EventHandler
  public void whenQuit(@NotNull final PlayerQuitEvent event) {
    this.input.get().onQuit(new PprQuitEvent(new PprChatSender(event.getPlayer())));
  }
}
