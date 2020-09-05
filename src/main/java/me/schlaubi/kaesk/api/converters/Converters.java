package me.schlaubi.kaesk.api.converters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.AbstractCommandClientBuilder;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of commonly used converters.
 *
 * @see ArgumentDeserializer
 */
public class Converters {

  private Converters() {

  }

  /**
   * Converter for Integer.
   */
  public static final ArgumentDeserializer<Integer> INTEGER = new NumberDeserializer<>(
      Integer::parseInt, Integer[]::new);

  /**
   * Converter for Long.
   */
  public static final ArgumentDeserializer<Long> LONG = new NumberDeserializer<>(
      Long::parseLong, Long[]::new);

  /**
   * Converter for Float.
   */
  public static final ArgumentDeserializer<Float> FLOAT = new NumberDeserializer<>(
      Float::parseFloat, Float[]::new);

  /**
   * Converter for Double.
   */
  public static final ArgumentDeserializer<Double> DOUBLE = new NumberDeserializer<>(
      Double::parseDouble, Double[]::new);

  /**
   * Converter for Short.
   */
  public static final ArgumentDeserializer<Short> SHORT = new NumberDeserializer<>(
      Short::parseShort, Short[]::new
  );

  public static final ArgumentDeserializer<String> STRING = new ArgumentDeserializer<String>() {

    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      return true;
    }

    @NotNull
    @Override
    public String deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      return input;
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(@NotNull String[] args, @NotNull Class<?> clazz) {
      return true;
    }

    @NotNull
    @Override
    public String[] deserializeVararg(@NotNull String[] args, @NotNull Class<?> clazz) {
      return args;
    }
  };

  /**
   * Creates a new {@link ArgumentDeserializer} that can deserialize {@link Enum}s. Example: {@code
   * builder.addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new)); }
   *
   * @param arrayGenerator an array generator for the enum type
   * @param <T> the type of the enum
   * @return the new deserializer
   * @see AbstractCommandClientBuilder#addDeserializer(Class, ArgumentDeserializer)
   */
  public static <T extends Enum<T>> ArgumentDeserializer<T> newEnumDeserializer(
      final IntFunction<T[]> arrayGenerator) {
    return new EnumDeserializer<>(arrayGenerator);
  }

  private static final class NumberDeserializer<T> implements ArgumentDeserializer<T> {

    private final Function<String, T> parser;
    private final IntFunction<T[]> arrayGenerator;
    private T output;
    private T[] varArgOutput;

    private NumberDeserializer(final Function<String, T> parser,
        final IntFunction<T[]> arrayGenerator) {
      this.parser = parser;
      this.arrayGenerator = arrayGenerator;
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      try {
        varArgOutput = Arrays.stream(args).map(parser).toArray(arrayGenerator);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public boolean isValid(@NotNull final String input, @NotNull final Class<?> clazz) {
      try {
        output = parser.apply(input);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public @NotNull T deserialize(@NotNull final String input, @NotNull final Class<?> clazz) {
      if (output == null) {
        throw new IllegalStateException(
            "This method is not supposed to be called when isValid returned false");
      }
      return output;
    }

    @Override
    public T[] deserializeVararg(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      if (varArgOutput == null) {
        throw new IllegalStateException(
            "This method is not supposed to be called when isValid returned false");
      }
      return varArgOutput;
    }
  }


  private static final class EnumDeserializer<T extends Enum<T>> implements
      ArgumentDeserializer<T> {

    private final IntFunction<T[]> arrayGenerator;

    private EnumDeserializer(final IntFunction<T[]> arrayGenerator) {
      this.arrayGenerator = arrayGenerator;
    }

    @Override
    public boolean isValid(@NotNull final String input, @NotNull final Class<?> clazz) {
      return Arrays.stream(clazz.getEnumConstants()).anyMatch(it -> it.toString().equals(input));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T deserialize(@NotNull final String input, @NotNull final Class<?> clazz) {
      Class<T> klass = (Class<T>) clazz;
      return Enum.valueOf(klass, input);
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      return Arrays.stream(args).allMatch(input -> Arrays.stream(clazz.getEnumConstants())
          .anyMatch(it -> it.toString().equals(input)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] deserializeVararg(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      return Arrays.stream(args).map(arg -> {
        Class<T> klass = (Class<T>) clazz;
        return Enum.valueOf(klass, arg);
      }).toArray(arrayGenerator);
    }

    @Override
    public @NotNull List<String> providePossibilities(@NotNull final String parameterName,
        final boolean isVarArg,
        final Class<?> clazz) {
      return Collections
          .unmodifiableList(Arrays.stream(clazz.getEnumConstants()).map(Object::toString)
              .collect(Collectors.toList()));
    }
  }


  public static abstract class PlayerDeserializer<T> implements
      ArgumentDeserializer<T> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");
    private final Function<T, String> nameFinder;

    protected PlayerDeserializer(Function<T, String> nameFinder) {
      this.nameFinder = nameFinder;
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      return Arrays.stream(args).allMatch(arg -> NAME_PATTERN.matcher(arg).matches());
    }

    @Override
    public boolean isValid(@NotNull final String input, @NotNull final Class<?> clazz) {
      return NAME_PATTERN.matcher(input).matches();
    }

    @Override
    public @NotNull List<String> providePossibilities(@NotNull final String parameterName,
        final boolean isVarArg,
        final Class<?> clazz) {
      return Collections.unmodifiableList(getPlayers().stream().map(nameFinder)
          .collect(Collectors.toList()));
    }


    protected abstract Collection<? extends T> getPlayers();
  }
}
