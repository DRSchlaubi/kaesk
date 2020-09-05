package me.schlaubi.kaesk.api;

import org.jetbrains.annotations.NotNull;

/**
 * Platform independent command sender.
 */
public interface CommandSender<T> {

  /**
   * Checks whether this sender has the specified permission.
   * @param permission the permission
   * @return whether the sender has the permission or not
   */
  boolean hasPermission(@NotNull String permission);

  /**
   * Checks whether the sender is the console or not
   * @return whether the sender is the console or not
   */
  boolean isConsole();

  /**
   * Sends the message to the sender
   * @param text the content of the message
   */
  void sendMessage(@NotNull String text);

  /**
   * Returns the actual platform sender.
   * @return the actual platform sender
   */
  @NotNull
  T getActual();
}
