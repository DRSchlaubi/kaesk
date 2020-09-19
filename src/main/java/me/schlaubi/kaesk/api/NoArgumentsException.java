package me.schlaubi.kaesk.api;

/**
 * Exception thrown to indicate that a command with no suitable arguments was requested but not
 * found and the used platform does not have a automatic usage system.
 * on spigot this exception will be caught and converted to a return false at command executor
 * level
 */
public class NoArgumentsException extends RuntimeException {

  /**
   * You should know what a constructor is.
   */
  public NoArgumentsException() {
    super(
        "If you wish to have a default command without any parameters please add one to your command class");
  }
}
