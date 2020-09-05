package me.schlaubi.kaesk.api;

import java.util.List;
import me.schlaubi.kaesk.internal.CompiledCommandClass;
import org.jetbrains.annotations.NotNull;

/**
 * Command client.
 *
 * @see AbstractCommandClientBuilder
 */
public interface CommandClient<SENDER> {

  /**
   * Registers a command.
   *
   * @param command the instance of the command executor
   */
  void registerCommand(@NotNull final Object command);

  /**
   * Registers a list of commands
   *
   * @param commands a list of command executors.
   */
  default void registerCommands(@NotNull final Object... commands) {
    for (Object command : commands) {
      registerCommand(command);
    }
  }

  /**
   * Unregisters a command.
   *
   * @param name the name of the command to unregister
   */
  void unregisterCommand(@NotNull String name);

  boolean onCommand(final CompiledCommandClass compiledCommand,
      final me.schlaubi.kaesk.api.CommandSender<SENDER> sender,
      final String label, final String[] args);

  List<String> onTabComplete(final CompiledCommandClass compiledCommand,
      final me.schlaubi.kaesk.api.CommandSender<SENDER> sender,
      final String alias, final String[] args);

}
