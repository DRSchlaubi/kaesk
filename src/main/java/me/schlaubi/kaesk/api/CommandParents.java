package me.schlaubi.kaesk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Annotation to specify super commands.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandParents {

  /**
   * Super commands.
   * @return an array of invokes of super commands
   */
  @NotNull
  String[] value();
}
