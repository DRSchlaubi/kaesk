# even-more-simple-spigot-command-thingy

![Demo video](https://rice.by.devs-from.asia/OrNX5gv9jh.gif)

This is a very simple Spigot command parser
**Info** This library only works when using java 14+ and at list this PaperMC build ([#229](https://papermc.io/api/v1/paper/1.15.2/229/download)) because of some wierd bugs in spigot code

## Usage
Make a command.
```java
@CommandClass(name = "gamemode", permission = "plugin.gamemode")
public class GameModeCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender) {
    sender.sendMessage("Usage: /gamemode <mode> <player>");
  }
}e
```

Register the command
```java
  @Override
  public void onEnable() {
    var commandClient = new CommandClientBuilder(this)
        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
        .setArgumentHandler((error, sender) -> sender.sendMessage(
            "Place enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
        .build();
    commandClient.registerCommand(new SumCommand());
  }
```

For more information take a look at the example [here](https://github.com/DRSchlaubi/kaesk/tree/master/example)

## Javadoc
The completely ugly javadoc (thx oracle) can be found [here](https://drschlaubi.github.io/mgisdumb)

## Download
You can get the latest version from [bintray](https://bintray.com/drschlaubi/maven/kaesk)

## Credits
Thanks to [Paul2708](https://github.com/Paul2708) for creating [simple-commands](https://github.com/Paul2708/simple-commands) as I kinda copyed his idea.

Thanks to [mgvpri](https://twitch.tv) for doing something simmilar in his stream
