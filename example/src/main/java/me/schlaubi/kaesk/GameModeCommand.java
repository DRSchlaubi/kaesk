package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandClass;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandClass(name = "gamemode", permission = "plugin.gamemode")
public class GameModeCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender) {
    sender.sendMessage("Usage: /gamemode <mode> <player>");
  }

  @Command(root = true)
  public void rootCommand(Player player, GameMode gameMode) {
    player.setGameMode(gameMode);
    player.sendMessage(String.format("Your gamemode has been changed to %s", gameMode));
  }

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender, GameMode gameMode, Player target) {
    target.setGameMode(gameMode);
    target.sendMessage(String.format("Your gamemode has been changed to %s", gameMode));
    sender.sendMessage(
        String.format("The gamemode of %s has been set to %s", target.getName(), gameMode));
  }
}
