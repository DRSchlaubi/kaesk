package me.schlaubi.kaesk;

import java.util.ArrayList;
import java.util.List;
import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

@CommandClass(name = "vanish", permission = "plugin.vanish")
public class VanishCommand implements Listener {

  private final List<Player> vanishedPlayers = new ArrayList<>();
  private final JavaPlugin plugin;

  public VanishCommand(JavaPlugin plugin) {
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @Command(root = true)
  public void vanishCommand(Player target) {
    toggleVanish(target, target);
  }

  @Command(root = true, consoleAllowed = true)
  public void vanishCommand(CommandSender sender, Player target) {
    toggleVanish(sender, target);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    var joinedPlayer = event.getPlayer();
    if (!vanishedPlayers.contains(joinedPlayer)) {
      hidePlayers(joinedPlayer);
    }
  }

  private void hidePlayers(Player target) {
    vanishedPlayers.forEach(player -> target.hidePlayer(plugin, player));
  }

  private void toggleVanish(CommandSender sender, Player target) {
    if (vanishedPlayers.contains(target)) {
      unvanishPlayer(target);
      if (sender.equals(target)) {
        sender.sendMessage("You're now unvanished");
      } else {
        sender.sendMessage(String.format("You've unvanished %s", target));
        target.sendMessage(String.format("You've been unvanished by %s", target));
      }
    } else {
      vanishPlayer(target);
      if (sender.equals(target)) {
        sender.sendMessage("You're now vanished");
      } else {
        sender.sendMessage(String.format("You've vanished %s", target));
        target.sendMessage(String.format("You've been vanished by %s", target));
      }
    }
  }

  private void vanishPlayer(Player target) {
    vanishedPlayers.add(target);
    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
      if (vanishedPlayers.contains(onlinePlayer)) {
        target.showPlayer(plugin, onlinePlayer);
      } else {
        onlinePlayer.hidePlayer(plugin, target);
      }
    });
  }

  private void unvanishPlayer(Player target) {
    vanishedPlayers.remove(target);
    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
      if (vanishedPlayers.contains(onlinePlayer)) {
        target.hidePlayer(plugin, onlinePlayer);
      } else {
        onlinePlayer.showPlayer(plugin, target);
      }
    });
  }
}
