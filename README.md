[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)
[![rultor](https://www.rultor.com/b/yegor256/rultor)](https://www.rultor.com/p/portlek/input)

[![Build Status](https://travis-ci.com/portlek/input.svg?branch=master)](https://travis-ci.com/portlek/input)
![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/input?label=version)
## How to Use
```gradle
// For Bukkit projects.
implementation("io.github.portlek:input-bukkit:${version}")
// For Nukkit projects.
implementation("io.github.portlek:input-nukkit:${version}")
```
```xml
<!-- For Bukkit projects. -->
<dependency>
  <groupId>io.github.portlek</groupId>
  <artifactId>input-bukkit</artifactId>
  <version>${version}</version>
</dependency>
<!-- For Nukkit projects. -->
<dependency>
  <groupId>io.github.portlek</groupId>
  <artifactId>input-nukkit</artifactId>
  <version>${version}</version>
</dependency>
```
## Example usage
```java
public final class TestCommand implements CommandExecutor {

  private final Plugin plugin;
    
  public TestCommand(final Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) { // PlayerChatInput only works with players
      sender.sendMessage("Only for players");
      return false;
    }
    // This comand will ask for a number n and will send to the player n! so we will
    // work with integers
    // BukkitChatInputBuilder.integer(..., ...).otherStuffs();
    final BukkitChatInput<Integer> chatInput = BukkitChatInputBuilder.builder(this.plugin, player)
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

  private long factorialOf(final int num) {
    return num <= 1 ? 1 : this.factorialOf(num - 1) * num;
  }
```