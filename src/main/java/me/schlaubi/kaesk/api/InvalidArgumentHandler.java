package me.schlaubi.kaesk.api;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for handling invalid arguments.
 */
@FunctionalInterface
public interface InvalidArgumentHandler {

  /**
   * Handles an {@link ArgumentException}
   * @implNote This method is being invoked when an {@link ArgumentDeserializer} returns false at {@link ArgumentDeserializer#isValid(String, Class)} or {@link ArgumentDeserializer#varargIsValid(String[], Class)}
   * @param exception the exception that occurred
   * @param sender the {@link CommandSender} that executed the command
   */
  void handleInvalidArgument(@NotNull ArgumentException exception, @NotNull CommandSender sender);
}
