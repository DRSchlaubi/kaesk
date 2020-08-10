package me.schlaubi.kaesk.api;

import org.jetbrains.annotations.NotNull;

/**
 * Command client.
 *
 * @see me.schlaubi.kaesk.api.CommandClientBuilder
 */
public interface CommandClient {

  /**
   * Registers a command.
   *
   * @param command the instance of the command executor
   */
  void registerCommand(@NotNull Object command);

  /**
   * Registers a list of commands
   * @param commands a list of command executors.
   */
  default void registerCommands(@NotNull Object... commands) {
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
}
