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

package io.github.portlek.input.bukkit;

import io.github.portlek.input.CoreChatInput;
import io.github.portlek.input.Sender;
import io.github.portlek.input.Task;
import io.github.portlek.input.bukkit.impl.BkktChatEvent;
import io.github.portlek.input.bukkit.impl.BkktQuitEvent;
import io.github.portlek.input.bukkit.impl.BkktTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an implementation for {@link io.github.portlek.input.ChatInput}.
 *
 * @param <T> the value type.
 */
public final class BukkitChatInput<T> extends CoreChatInput<T, Player> implements Listener {

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

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
  BukkitChatInput(@NotNull final Plugin plugin, @NotNull final Sender<Player> sender,
                  @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                  @NotNull final BiPredicate<Sender<Player>, String> isValidInput,
                  @NotNull final BiFunction<Sender<Player>, String, T> setValue,
                  @NotNull final BiConsumer<Sender<Player>, T> onFinish,
                  @NotNull final Consumer<Sender<Player>> onCancel, @NotNull final Consumer<Sender<Player>> onExpire,
                  @NotNull final String cancel, @NotNull final BiPredicate<Sender<Player>, String> onInvalidInput,
                  final boolean repeat, final long expire) {
    super(cancel, expire, invalidInputMessage, isValidInput, onCancel, onExpire, onFinish, onInvalidInput, repeat,
      sendValueMessage, sender, setValue);
    this.plugin = plugin;
  }

  @NotNull
  @Override
  public Task createRunTaskLater(@NotNull final Runnable runnable, final long time) {
    return new BkktTask(Bukkit.getScheduler().runTaskLater(this.plugin, runnable, time));
  }

  @Override
  public void registerEvent() {
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
  public void whenChat(@NotNull final AsyncPlayerChatEvent event) {
    this.onChat(new BkktChatEvent(event));
  }

  /**
   * runs when the player quits the game.
   *
   * @param event the event to handle.
   */
  @EventHandler
  public void whenQuit(@NotNull final PlayerQuitEvent event) {
    this.onQuit(new BkktQuitEvent(event));
  }
}
