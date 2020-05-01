package me.schlaubi.kaesk.api;

import org.jetbrains.annotations.NotNull;

/**
 * An exception that is thrown when an invalid argument was entered.
 */
public class ArgumentException extends IllegalArgumentException {

  private final ArgumentDeserializer<?> deserializer;
  private final Class<?> parameterType;

  /**
   * Constructs new ArgumentException.
   *
   * @param deserializer the deserializer that reported the wrong argument
   * @param parameterType the type of the argument
   */
  public ArgumentException(@NotNull ArgumentDeserializer<?> deserializer,
      @NotNull Class<?> parameterType) {
    this.deserializer = deserializer;
    this.parameterType = parameterType;
  }

  /**
   * Constructs new ArgumentException.
   *
   * @param deserializer the deserializer that reported the wrong argument
   * @param parameterType the type of the argument
   * @param message the message of this exception
   * @see Exception#Exception(String)
   */
  public ArgumentException(@NotNull String message, @NotNull ArgumentDeserializer<?> deserializer,
      Class<?> parameterType) {
    super(message);
    this.deserializer = deserializer;
    this.parameterType = parameterType;
  }

  /**
   * Constructs new ArgumentException.
   *
   * @param deserializer the deserializer that reported the wrong argument
   * @param parameterType the type of the argument
   * @param cause the cause of this exception
   * @param message the message of this exception
   * @see Exception#Exception(String, Throwable)
   */
  public ArgumentException(@NotNull String message, @NotNull Throwable cause,
      @NotNull ArgumentDeserializer<?> deserializer, @NotNull Class<?> parameterType) {
    super(message, cause);
    this.deserializer = deserializer;
    this.parameterType = parameterType;
  }

  /**
   * Constructs new ArgumentException.
   *
   * @param deserializer the deserializer that reported the wrong argument
   * @param parameterType the type of the argument
   * @param cause the cause of this exception
   * @see Exception#Exception(Throwable)
   */
  public ArgumentException(@NotNull Throwable cause,
      @NotNull ArgumentDeserializer<?> deserializer, @NotNull Class<?> parameterType) {
    super(cause);
    this.deserializer = deserializer;
    this.parameterType = parameterType;
  }

  /**
   * Returns the reporting {@link ArgumentDeserializer}.
   *
   * @return the reporting {@link ArgumentDeserializer}
   */
  @NotNull
  public ArgumentDeserializer<?> getDeserializer() {
    return deserializer;
  }

  /**
   * Returns the type of the parameter.
   *
   * @return the type of the parameter
   */
  @NotNull
  public Class<?> getParameterType() {
    return parameterType;
  }
}
