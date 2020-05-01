package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandClient implements CommandClient {

  private final CommandClassCompiler compiler;
  private final JavaPlugin plugin;
  private final TabCompleter tabCompleter;
  private final CommandExecutor commandExecutor;

  public DefaultCommandClient(
      Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      JavaPlugin plugin, InvalidArgumentHandler argumentHandler) {
    this.compiler = new CommandClassCompiler(deserializers);
    this.plugin = plugin;
    this.tabCompleter = new TabCompleter(deserializers);
    this.commandExecutor = new CommandExecutor(deserializers, argumentHandler);
  }

  /* package-private */ boolean onCommand(CompiledCommandClass compiledCommand,
      CommandSender sender,
      Command command, String label, final String[] args) {
    return commandExecutor.onCommand(compiledCommand, sender, command, label, args);
  }

  /* package-private */ List<String> onTabComplete(CompiledCommandClass compiledCommand,
      CommandSender sender,
      Command command, String alias, String[] args) {
    return tabCompleter.onTabComplete(compiledCommand, sender, command, alias, args);
  }

  @Override
  public void registerCommand(@NotNull Object command) {
    Preconditions.checkNotNull(command, "Command may not be null");
    var compiledCommand = compiler.compile(command);
    System.out.println("Built command tree: %s".formatted(compiledCommand.commandTree()));
    var bukkitCommand = plugin.getCommand(compiledCommand.name());
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(new DelegatingCommand(this, compiledCommand));
  }

  @Override
  public void unregisterCommand(@NotNull String name) {
    Preconditions.checkNotNull(name, "Command name may not be null");
    var bukkitCommand = plugin.getCommand(name);
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(null);
  }
}
