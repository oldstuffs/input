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

import io.github.portlek.input.ChatInputPlugin;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link ChatInputPlugin}.
 */
public final class PprPlugin implements ChatInputPlugin<BukkitTask, Listener> {

  /**
   * the scheduler.
   */
  @NotNull
  private final BiFunction<Runnable, Long, BukkitTask> createRunTaskLater;

  /**
   * the plugin manager.
   */
  @NotNull
  private final Consumer<Listener> registerEvent;

  /**
   * ctor.
   *
   * @param plugin the plugin.
   */
  public PprPlugin(@NotNull final Plugin plugin) {
    final PluginManager pluginManager = plugin.getServer().getPluginManager();
    final BukkitScheduler scheduler = plugin.getServer().getScheduler();
    this.registerEvent = listener -> pluginManager.registerEvents(listener, plugin);
    this.createRunTaskLater = (runnable, time) -> scheduler.runTaskLater(plugin, runnable, time);
  }

  @NotNull
  @Override
  public BukkitTask createRunTaskLater(@NotNull final Runnable runnable, final long time) {
    return this.createRunTaskLater.apply(runnable, time);
  }

  @Override
  public void registerEvent(@NotNull final Listener listener) {
    this.registerEvent.accept(listener);
  }
}
