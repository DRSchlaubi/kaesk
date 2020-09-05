package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.bukkit.BukkitCommandClient;
import me.schlaubi.kaesk.api.bukkit.BukkitCommandClientBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModePlugin extends JavaPlugin {

  @SuppressWarnings("FieldCanBeLocal") // does not matter here
  private BukkitCommandClient commandClient;

  @Override
  public void onEnable() {
    commandClient = new BukkitCommandClientBuilder(this)
        // no longer needed since this is registered by default
//        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
        .setArgumentHandler((error, sender) -> sender.sendMessage(
            "Please enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
        .setNoPermissionHandler((sender, permission) -> sender.sendMessage("You need the permission %s to proceed".formatted(permission)))
        .build();
    commandClient.registerCommand(new GameModeCommand());
    commandClient.registerCommand(new SayCommand());
    commandClient.registerCommand(new SumCommand());
    commandClient.registerCommand(new SubSubTestCommand());
    commandClient.registerCommand(new VanishCommand(this));
  }
}
