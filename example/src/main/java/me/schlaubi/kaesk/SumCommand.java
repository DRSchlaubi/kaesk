package me.schlaubi.kaesk;

import java.util.stream.Stream;
import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import org.bukkit.command.CommandSender;

@CommandClass(name = "sum")
public class SumCommand {

  @Command(root = true, consoleAllowed = true)
  public void rootCommand(CommandSender sender, @CommandArgument(name = "numbers") Integer... numbers) {
    sender.sendMessage(String.valueOf(Stream.of(numbers).mapToInt(Integer::intValue).sum()));
  }
}
