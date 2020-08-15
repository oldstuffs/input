/*
 * MIT License
 *
 * Copyright (c) 2020 MrNemo64
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

import io.github.portlek.input.ChatInputPlugin;
import io.github.portlek.input.CoreChatInput;
import io.github.portlek.input.Task;
import io.github.portlek.input.bukkit.impl.BkktChatEvent;
import io.github.portlek.input.bukkit.impl.BkktQuitEvent;
import io.github.portlek.input.bukkit.impl.BkktSender;
import io.github.portlek.input.bukkit.impl.BkktTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitChatInput<T> extends CoreChatInput<T, Player, BkktSender, BukkitTask, BkktChatEvent,
    BkktQuitEvent, Listener> implements Listener {

    public BukkitChatInput(@NotNull final ChatInputPlugin<BukkitTask, Listener> plugin,
                           @NotNull final BkktSender sender, @Nullable final String invalidInputMessage,
                           @Nullable final String sendValueMessage,
                           @NotNull final BiFunction<BkktSender, String, Boolean> isValidInput,
                           @NotNull final BiFunction<BkktSender, String, T> setValue,
                           @NotNull final BiConsumer<BkktSender, T> onFinish,
                           @NotNull final Consumer<BkktSender> onCancel, @NotNull final Consumer<BkktSender> onExpire,
                           @NotNull final String cancel,
                           @NotNull final BiFunction<BkktSender, String, Boolean> onInvalidInput, final boolean repeat,
                           final long expire) {
        super(plugin, sender, invalidInputMessage, sendValueMessage, isValidInput, setValue, onFinish, onCancel,
            onExpire, cancel, onInvalidInput, repeat, expire);
    }

    @NotNull
    @Override
    public Task<BukkitTask> createTask(@NotNull final BukkitTask task) {
        return new BkktTask(task);
    }

    @EventHandler
    public void whenQuit(@NotNull final PlayerQuitEvent event) {
        this.onQuit(new BkktQuitEvent(event));
    }

    @EventHandler
    public void whenChat(@NotNull final AsyncPlayerChatEvent event) {
        this.onChat(new BkktChatEvent(event));
    }

    @NotNull
    @Override
    public BukkitChatInput<T> self() {
        return this;
    }

    @Override
    public void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }

}
