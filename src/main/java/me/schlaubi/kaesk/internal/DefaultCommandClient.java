package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandClient implements CommandClient {

  private final CommandClassCompiler compiler;
  private final JavaPlugin plugin;
  private final TabCompleter tabCompleter;
  private final CommandExecutor commandExecutor;

  public DefaultCommandClient(
      final Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      final JavaPlugin plugin, InvalidArgumentHandler argumentHandler,
      final NoPermissionHandler noPermissionHandler) {
    this.compiler = new CommandClassCompiler(deserializers);
    this.plugin = plugin;
    this.tabCompleter = new TabCompleter(deserializers);
    this.commandExecutor = new CommandExecutor(deserializers, argumentHandler, noPermissionHandler);
  }

  boolean onCommand(final CompiledCommandClass compiledCommand,
      final CommandSender sender,
      final Command command, final String label, final String[] args) {
    return commandExecutor.onCommand(compiledCommand, sender, command, label, args);
  }

  List<String> onTabComplete(final CompiledCommandClass compiledCommand,
      final CommandSender sender,
      final Command command, final String alias, final String[] args) {
    return tabCompleter.onTabComplete(compiledCommand, sender, command, alias, args);
  }

  @Override
  public void registerCommand(@NotNull final Object command) {
    Preconditions.checkNotNull(command, "Command may not be null");
    final CompiledCommandClass compiledCommand = compiler.compile(command);
    final PluginCommand bukkitCommand = plugin.getCommand(compiledCommand.getName());
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(new DelegatingCommand(this, compiledCommand));
  }

  @Override
  public void unregisterCommand(@NotNull String name) {
    Preconditions.checkNotNull(name, "Command name may not be null");
    final PluginCommand bukkitCommand = plugin.getCommand(name);
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(null);
  }
}
