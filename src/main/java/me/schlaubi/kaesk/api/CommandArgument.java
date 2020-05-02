package me.schlaubi.kaesk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Annotations that can be used to specify parameter options.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArgument {

  /**
   * The name of the parameter in help messages.
   *
   * @return the name
   */
  @NotNull
  String name();

}
