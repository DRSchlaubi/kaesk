package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import me.schlaubi.kaesk.api.bungee.BungeeCommandClient;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BungeeCommandClientImpl extends AbstractCommandClient<ProxiedPlayer, CommandSender> implements
    BungeeCommandClient {

  private final Plugin plugin;

  public BungeeCommandClientImpl(
      Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      InvalidArgumentHandler argumentHandler,
      NoPermissionHandler noPermissionHandler,
      Plugin plugin) {
    super(deserializers, argumentHandler, noPermissionHandler, CommandSender.class,
        ProxiedPlayer.class, plugin.getLogger());
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CompiledCommandClass compiledCommand,
      final me.schlaubi.kaesk.api.CommandSender<CommandSender> sender,
      final String label, final String[] args
  ) {
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
    final Command delegatingCommand = new DelegatingBungeeCommand(compiledCommand.name, this,
        compiledCommand);
    ProxyServer.getInstance().getPluginManager().registerCommand(plugin, delegatingCommand);
  }

  @Override
  public void unregisterCommand(@NotNull String name) {
    throw new UnsupportedOperationException("Only supported on spigot");
  }
}
