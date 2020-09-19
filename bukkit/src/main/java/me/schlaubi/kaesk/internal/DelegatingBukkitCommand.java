package me.schlaubi.kaesk.internal;

import java.util.Collections;
import java.util.List;
import me.schlaubi.kaesk.api.NoArgumentsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

class DelegatingBukkitCommand implements CommandExecutor, TabExecutor {

  private final BukkitCommandClientImpl commandClient;
  private final CompiledCommandClass compiledCommand;

  public DelegatingBukkitCommand(
      @NotNull BukkitCommandClientImpl commandClient,
      @NotNull CompiledCommandClass compiledCommand) {
    this.commandClient = commandClient;
    this.compiledCommand = compiledCommand;
  }

  @Override
  public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
      @NotNull final String label, @NotNull final String[] args) {
    try {
      return commandClient.onCommand(compiledCommand, new BukkitCommandSender(sender), label, args);
    } catch (NoArgumentsException e) {
      return false;
    }
  }

  @NotNull
  @Override
  public List<String> onTabComplete(@NotNull final CommandSender sender,
      @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
    try {
      return commandClient.onTabComplete(compiledCommand, new BukkitCommandSender(sender), alias, args);
    } catch (NoArgumentsException e) {
      return Collections.emptyList();
    }
  }

  private static class BukkitCommandSender implements me.schlaubi.kaesk.api.CommandSender<CommandSender> {

    private final CommandSender bukkitSender;

    private BukkitCommandSender(CommandSender bukkitSender) {
      this.bukkitSender = bukkitSender;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
      return bukkitSender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
      return this instanceof ConsoleCommandSender;
    }

    @Override
    public void sendMessage(@NotNull String text) {
      bukkitSender.sendMessage(text);
    }

    @Override
    public @NotNull CommandSender getActual() {
      return bukkitSender;
    }
  }
}
