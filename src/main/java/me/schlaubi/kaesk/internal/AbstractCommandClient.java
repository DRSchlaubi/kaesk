package me.schlaubi.kaesk.internal;

import java.util.Map;
import java.util.logging.Logger;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import org.jetbrains.annotations.NotNull;

abstract class AbstractCommandClient<PLAYER, SENDER> implements CommandClient<SENDER> {

  protected final CommandClassCompiler compiler;
  protected final TabCompleter tabCompleter;
  protected final CommandExecutor<PLAYER> commandExecutor;

  public AbstractCommandClient(
      @NotNull final Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      @NotNull InvalidArgumentHandler argumentHandler,
      @NotNull final NoPermissionHandler noPermissionHandler,
      @NotNull final Class<?> commandSenderClass,
      @NotNull Class<PLAYER> playerClazz,
      @NotNull Logger logger
  ) {
    this.compiler = new CommandClassCompiler(deserializers, commandSenderClass);
    this.tabCompleter = new TabCompleter(deserializers);
    this.commandExecutor = new CommandExecutor<>(deserializers, argumentHandler,
        noPermissionHandler,
        playerClazz, logger);
  }
}
