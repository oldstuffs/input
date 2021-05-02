[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

![master](https://github.com/portlek/input/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/input-common?label=version)

## How to Use

```xml
<!-- For Bukkit projects. -->
<dependency>
  <groupId>io.github.portlek</groupId>
  <artifactId>input-bukkit</artifactId>
  <version>${version}</version>
</dependency>
  <!-- For Paper projects. -->
<dependency>
<groupId>io.github.portlek</groupId>
<artifactId>input-paper</artifactId>
<version>${version}</version>
</dependency>
```

```groovy
// For Bukkit projects.
implementation("io.github.portlek:input-bukkit:${version}")
// For Paper projects.
implementation("io.github.portlek:input-paper:${version}")
```

## Example usage

```java
@RequiredArgsConstructor
public final class TestCommand implements CommandExecutor {

  @NotNull
  private final Plugin plugin;

  @NotNull
  private final PaperChatPlatform platform = new PaperChatPlatform(this.plugin);

  @Override
  public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                           @NotNull final String label, @NotNull final String[] args) {
    // Input only works with players
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only for players");
      return false;
    }
    PaperChatPlatform.<Integer>builder(this.platform, (Player) sender)
      // Set the validation
      .isValidInput((player, input) -> {
        try {
          // We only accept numbers greater than 0
          return Integer.parseInt(input) > 0;
        } catch (final Exception e) {
          // The input was not an integer
          return false;
        }
      })
      .setValue((player, input) -> {
        // We convert the input string to a number
        return Integer.parseInt(input);
      })
      .onInvalidInput((player, input) -> {
        // Send a message if the input is invalid
        player.sendMessage("That is not a number");
        // Send the messages established with invalidInputMessage(String) and sendValueMessage(String)
        return true;
      })
      .onFinish((player, value) -> {
        // When the player inputs a string that is a number greater that 0 we send a message
        player.sendMessage(value + "! is " + this.factorialOf(value));
      })
      .onCancel(player -> {
        // If the player cancels, we send a message
        player.sendMessage("Canceled the factorial-calculation");
      })
      .onExpire(player -> {
        // If the input time expires.
        player.sendMessage("Input expired!");
      })
      .expire(20L * 30L)
      .repeat(true)
      // Message if the input is invalid
      .invalidInputMessage("That is not a number/Can calculate the factorial of it")
      // Asking for the number
      .sendValueMessage("Send a number to calculate")
      .toCancel("cancel")
      .build()
      .start();
    return true;
  }

  private long factorialOf(final int num) {
    return num <= 1 ? 1 : this.factorialOf(num - 1) * num;
  }
}
```
