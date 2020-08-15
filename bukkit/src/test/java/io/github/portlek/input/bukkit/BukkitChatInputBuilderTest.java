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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class BukkitChatInputBuilderTest {

    private final Plugin plugin = Mockito.mock(Plugin.class);

    private final Player player = Mockito.mock(Player.class);

    @Test
    void builder() {
        final BukkitChatInput<Integer> build = BukkitChatInputBuilder.<Integer>builder(this.plugin, player)
            .isValidInput((player, input) -> { // Set the validation
                try {
                    return Integer.parseInt(input) > 0; // We only accept numbers greater than 0
                } catch (Exception e) {
                    return false; // The input was not an integer
                }
            }).setValue((player, input) -> {
                // We convert the input string to a number
                return Integer.parseInt(input);
            }).onInvalidInput((player, input) -> {
                // Send a message if the input is invalid
                player.sendMessage("That is not a number");
                // Send the messages stablished with invalidInputMessage(String) and sendValueMessage(String)
                return true;
            }).onFinish((player, value) -> {
                // when the player inputs a string that is a number greater that 0 we send a message
                player.sendMessage(value + "! is " + this.factorialOf(value));
            }).onCancel(player -> {
                // if the player cancels, we send a message
                player.sendMessage("Canceled the factorial-calculation");
            }).onExpire(player -> {
                // if the input time expires.
                player.sendMessage("Input expired!");
            }).expire(20L * 30L)
            .repeat(true)
            .invalidInputMessage("That is not a number/Can calculate the factorial of it")// Message if the input is invalid
            .sendValueMessage("Send a number to calculate") // Asking for the number
            .toCancel("cancel")
            .build();
    }

    @Test
    void build() {
        final BukkitChatInputBuilder<Integer> builder = new BukkitChatInputBuilder<>(this.plugin, this.player);
    }

    private long factorialOf(final int num) {
        return num <= 1 ? 1 : this.factorialOf(num - 1) * num;
    }

}