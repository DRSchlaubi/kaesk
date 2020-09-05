# even-more-simple-spigot-command-thingy

![Demo video](https://rice.by.devs-from.asia/OrNX5gv9jh.gif)

This is a very simple Spigot command parser
**Info** This library only works when using java 14+ and at list this PaperMC build ([#229](https://papermc.io/api/v1/paper/1.15.2/229/download)) because of some wierd bugs in spigot code

## Usage
Make a command.

### Root command
```java
@CommandClass(name = "gamemode", permission = "plugin.gamemode")
public class GameModeCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender) {
    sender.sendMessage("Usage: /gamemode <mode> <player>");
  }
}
```

### Sub command

```java
@Command(name = "sub")
public void subCommand(Player player) {
  // because console is not allowed first argument ist auto-casted to player
}
```

### NSub command
```java
@CommandParents({"sub"})
@Command(name = "sub")
public void subCommand(Player player, @CommandParameter(name = "playerName") String playerName) {
  // parameter names get discarded at compile time so you can use an annotations
}
```

## Modules
- [Bukkit](https://github.com/DRSchlaubi/kaesk/tree/master/bukkit)
- [Bungee](https://github.com/DRSchlaubi/kaesk/tree/master/bukkit)


For more information take a look at the example [here](https://github.com/DRSchlaubi/kaesk/tree/master/example)

## Javadoc
The very ~~urgly javadoc (thx oracle)~~ fancy kdoc (thx jetbrains) can be found [here](https://p.mik.wtf/kaesk)

## Download
 Please download from [Modules](#modules)

## Credits
Thanks to [Paul2708](https://github.com/Paul2708) for creating [simple-commands](https://github.com/Paul2708/simple-commands) as I kinda copyed his idea.

Thanks to [mgvpri](https://twitch.tv) for doing something simmilar in his stream
