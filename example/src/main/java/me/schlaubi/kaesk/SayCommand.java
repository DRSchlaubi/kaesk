package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandClass(name = "say")
public class SayCommand {

  @Command(root = true, permission = "example.say", consoleAllowed = true)
  public void rootCommand(CommandSender sender) {
    sender.sendMessage("Usage: /say [bold] <text>");
  }

  @Command(root = true, permission = "example.say", consoleAllowed = true)
  public void rootCommand(CommandSender sender, @CommandArgument(name = "text") String... text) {
    var joined = String.join(" ", text);
    Bukkit.broadcast(joined, "");
  }

  @Command(name = "bold", permission = "example.say", consoleAllowed = true)
  public void bold(CommandSender sender, @CommandArgument(name = "text") String... text) {
    var joined = String.join(" ", text);
    Bukkit.broadcast(ChatColor.BOLD + joined, "");
  }

}
