package me.schlaubi.kaesk.api;

import org.bukkit.command.CommandSender;

/**
 * Interface for permission handling.
 */
@FunctionalInterface
public interface NoPermissionHandler {

  /**
   * Handles when a player does not have the permission that is required to execute a command.
   * @param sender the sender of the command
   * @param permission the missing permission
   */
  void handleNoPermissions(CommandSender sender, String permission);
}
