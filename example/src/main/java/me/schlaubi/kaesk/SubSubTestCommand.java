package me.schlaubi.kaesk;

import java.util.Arrays;
import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandClass;
import me.schlaubi.kaesk.api.CommandParents;
import org.bukkit.command.CommandSender;

@CommandClass(name = "test")
public class SubSubTestCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender, String... args) {
    sender.sendMessage(Arrays.toString(args));
  }

  @Command(name = "sub", consoleAllowed = true)
  public void subCommand(CommandSender sender, String... args) {
    sender.sendMessage("sub: " + Arrays.toString(args));
  }

  @CommandParents({"sub"})
  @Command(name = "sub", consoleAllowed = true)
  public void subSubCommand(CommandSender sender, String... args) {
    sender.sendMessage("sub sub: " + Arrays.toString(args));
  }

  @CommandParents({"sub", "sub"})
  @Command(name = "sub", consoleAllowed = true)
  public void subSubSubCommand(CommandSender sender, String... args) {
    sender.sendMessage("sub sub sub: " + Arrays.toString(args));
  }
}
