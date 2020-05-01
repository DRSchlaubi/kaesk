package me.schlaubi.kaesk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Annotation that marks a method that invokes a command.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Command {

  /**
   * Permission node that inherits permission from super command.
   */
  @NotNull
  String INHERIT_PERMISSION = "<@inherit>";

  /**
   * Whether this is the root command or not.
   *
   * @return this is the root command
   */
  boolean root() default false;

  /**
   * The name of the command
   *
   * @return the name of the command
   */
  @NotNull
  String name() default "";

  /**
   * The permission that is needed to execute the command
   *
   * @return the permission
   * @implNote default is a blank string for no permission
   */
  @NotNull
  String permission() default INHERIT_PERMISSION;

  /**
   * Whether the console should be allowed to execute this command or not.
   *
   * @return whether console can execute the command or not
   */
  boolean consoleAllowed() default false;
}
