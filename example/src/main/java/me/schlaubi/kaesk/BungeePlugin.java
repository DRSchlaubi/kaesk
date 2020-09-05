package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import me.schlaubi.kaesk.api.bungee.BungeeCommandClientBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

  @Override
  public void onEnable() {
    var commandClient = new BungeeCommandClientBuilder(this)
        // no longer needed since this is registered by default
//        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
        .setArgumentHandler((error, sender) -> sender.sendMessage(
            "Please enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
        .setNoPermissionHandler((sender, permission) -> sender
            .sendMessage("You need the permission %s to proceed".formatted(permission)))
        .build();

    commandClient.registerCommand(new GuildCommand());
  }

  @CommandClass(name = "guild", permission = "permission")
  public static class GuildCommand {

    @Command(name = "erstellen")
    public void createCommand(ProxiedPlayer sender, @CommandArgument(name = "name") String name,
        @CommandArgument(name = "tag") String tag) {
      sender.sendMessage(new TextComponent((name + tag)));
    }

    @Command(root = true)
    public void rootCommand(ProxiedPlayer sender, ProxiedPlayer other) {
      sender.sendMessage(new TextComponent(other.getName()));
    }
  }
}
