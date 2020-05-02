package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.CommandClientBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModePlugin extends JavaPlugin {

  private CommandClient commandClient;

  @Override
  public void onEnable() {
    commandClient = new CommandClientBuilder(this)
        // no longer needed since this is registered by default
//        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
        .setArgumentHandler((error, sender) -> sender.sendMessage(
            "Place enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
        .setNoPermissionHandler((sender, permission) -> sender.sendMessage("You need the permission %s to proceed".formatted(permission)))
        .build();
    commandClient.registerCommand(new GameModeCommand());
    commandClient.registerCommand(new SayCommand());
    commandClient.registerCommand(new SumCommand());
    commandClient.registerCommand(new SubSubTestCommand());
  }
}
