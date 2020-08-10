package me.schlaubi.kaesk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the {@link ArgumentDeserializer} to use.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UseDeserializer {

  /**
   * @return the {@link Class} declaring the {@link ArgumentDeserializer} to use.
   */
  Class<? extends ArgumentDeserializer<?>> value();
}
