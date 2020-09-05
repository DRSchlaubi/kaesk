package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import me.schlaubi.kaesk.api.bukkit.BukkitCommandClient;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandClientImpl extends AbstractCommandClient<Player, CommandSender> implements
    BukkitCommandClient {

  private final JavaPlugin plugin;

  public BukkitCommandClientImpl(
      final Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      final JavaPlugin plugin, InvalidArgumentHandler argumentHandler,
      final NoPermissionHandler noPermissionHandler) {
    super(deserializers, argumentHandler, noPermissionHandler, CommandSender.class, Player.class,
        Bukkit.getLogger());
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CompiledCommandClass compiledCommand,
      final me.schlaubi.kaesk.api.CommandSender<CommandSender> sender,
      final String label, final String[] args) {
    return commandExecutor.onCommand(compiledCommand, sender, label, args);
  }

  @Override
  public List<String> onTabComplete(final CompiledCommandClass compiledCommand,
      final me.schlaubi.kaesk.api.CommandSender<CommandSender> sender,
      final String alias, final String[] args) {
    return tabCompleter
        .onTabComplete(compiledCommand, sender, alias, args);
  }

  @Override
  public void registerCommand(@NotNull final Object command) {
    Preconditions.checkNotNull(command, "Command may not be null");
    final CompiledCommandClass compiledCommand = compiler.compile(command);
    final PluginCommand bukkitCommand = plugin.getCommand(compiledCommand.getName());
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(new DelegatingBukkitCommand(this, compiledCommand));
  }

  @Override
  public void unregisterCommand(@NotNull String name) {
    Preconditions.checkNotNull(name, "Command name may not be null");
    final PluginCommand bukkitCommand = plugin.getCommand(name);
    Preconditions.checkNotNull(bukkitCommand, "Command is null! Forgot plugin.yml?");
    bukkitCommand.setExecutor(null);
  }
}
