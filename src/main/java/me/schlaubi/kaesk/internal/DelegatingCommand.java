package me.schlaubi.kaesk.internal;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

class DelegatingCommand implements CommandExecutor, TabExecutor {

  private final DefaultCommandClient commandClient;
  private final CompiledCommandClass compiledCommand;

  public DelegatingCommand(@NotNull DefaultCommandClient commandClient,
      @NotNull CompiledCommandClass compiledCommand) {
    this.commandClient = commandClient;
    this.compiledCommand = compiledCommand;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    return commandClient.onCommand(compiledCommand, sender, command, label, args);
  }

  @NotNull
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender,
      @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    return commandClient.onTabComplete(compiledCommand, sender, command, alias, args);
  }

}
