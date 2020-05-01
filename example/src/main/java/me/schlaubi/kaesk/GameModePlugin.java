package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.CommandClientBuilder;
import me.schlaubi.kaesk.api.converters.Converters;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModePlugin extends JavaPlugin {

  private CommandClient commandClient;

  @Override
  public void onEnable() {
    commandClient = new CommandClientBuilder(this)
        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
        .setArgumentHandler((error, sender) -> sender.sendMessage(
            "Place enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
        .build();
    commandClient.registerCommand(new GameModeCommand());
    commandClient.registerCommand(new SayCommand());
    commandClient.registerCommand(new SumCommand());
    commandClient.registerCommand(new SubSubTestCommand());
  }
}
