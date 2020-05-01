package me.schlaubi.kaesk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Annotation that marks a class containing commands.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CommandClass {

  /**
   * The name of the command.
   * @return the name of the command
   */
  @NotNull
  String name();

  /**
   * The permission that is needed to execute the command.
   * @implNote default is a blank string for no permission
   * @return the permission
   */
  @NotNull
  String permission() default "";

}
