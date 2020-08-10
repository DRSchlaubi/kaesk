package me.schlaubi.kaesk.api;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Deserializer for command arguments.
 *
 * @param <T> the type of argument
 */
public interface ArgumentDeserializer<T> {

  /**
   * Checkst whether the input is of type {@code T} or not.
   *
   * @param input the input
   * @param clazz the {@link Class} of the parameter type
   * @return whether the input is valid or not
   */
  boolean isValid(@NotNull final String input, @NotNull final Class<?> clazz);

  /**
   * Converts the input the the given type. Only gets called when {@link #isValid(String, Class)}
   * returned true
   *
   * @param input the input
   * @param clazz the {@link Class} of the parameter type
   * @return the converted input
   */
  @NotNull
  T deserialize(@NotNull final String input, @NotNull final Class<?> clazz);

  /**
   * Returns whether this deserializer supports var-args.
   *
   * @return whether this deserializer supports var-args.
   */
  boolean supportsVararg();

  /**
   * Checks if var-arg is valid.
   *
   * @param args the string input
   * @param clazz the {@link Class} of the parameter type
   * @return the deserialized vararg
   * @implNote this is only being called when {@link #supportsVararg()} returned true
   */
  boolean varargIsValid(final @NotNull String[] args, final @NotNull Class<?> clazz);

  /**
   * Deserializes a vararg.
   *
   * @param args the string input
   * @param clazz the {@link Class} of the parameter type
   * @return the deserialized vararg
   * @implNote this is only being called when {@link #supportsVararg()} and {@link
   * #varargIsValid(String[], Class)} returned true
   */
  @NotNull
  T[] deserializeVararg(@NotNull final String[] args, @NotNull final Class<?> clazz);

  /**
   * Provides possibilities for tab complete
   *
   * @param parameterName the name of the parameter to provide possibilities for
   * @param isVarArg whether the parameter is a vararg or not
   * @param clazz the {@link Class} of the parameter
   * @return a list of possible arguments
   */
  @NotNull
  default List<String> providePossibilities(@NotNull final String parameterName,
      final boolean isVarArg,
      Class<?> clazz) {
    return Collections.unmodifiableList(
        Collections.singletonList(String.format("<%s%s>", parameterName, isVarArg ? "..." : "")));
  }
}
