package me.schlaubi.kaesk;

import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import org.bukkit.command.CommandSender;

@CommandClass(name = "surr")
public class SurroundedCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender, String a,
      @CommandArgument(surrounded = "\"", name = "b") String b, String c) {
    sender.sendMessage("""
        A: %s B: "%s" C: %s
        """.formatted(a, b, c));
  }
}
