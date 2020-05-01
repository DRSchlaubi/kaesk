package me.schlaubi.kaesk.api.converters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of commonly used converters.
 *
 * @see ArgumentDeserializer
 */
public class Converters {

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
   * Converter for Player.
   */
  public static final ArgumentDeserializer<Player> PLAYER = new OnlinePlayerDeserializer();

  /**
   * Converter for OfflinePlayer.
   */
  public static final ArgumentDeserializer<OfflinePlayer> OFFLINE_PLAYER = new OfflinePlayerDeserializer();

  public static final ArgumentDeserializer<String> STRING = new ArgumentDeserializer<>() {
    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      return true;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      return input;
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(String[] args, Class<?> clazz) {
      return true;
    }

    @Override
    public String[] deserializeVararg(String[] args, @NotNull Class<?> clazz) {
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
   * @see me.schlaubi.kaesk.api.CommandClientBuilder#addDeserializer(Class, ArgumentDeserializer)
   */
  public static <T extends Enum<T>> ArgumentDeserializer<T> newEnumDeserializer(
      IntFunction<T[]> arrayGenerator) {
    return new EnumDeserializer<>(arrayGenerator);
  }

  private static abstract class PlayerDeserializer<T extends OfflinePlayer> implements
      ArgumentDeserializer<T> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(String[] args, Class<?> clazz) {
      return Arrays.stream(args).allMatch(arg -> NAME_PATTERN.matcher(arg).matches());
    }

    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      return NAME_PATTERN.matcher(input).matches();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull List<String> providePossibilities(@NotNull String parameterName, boolean isVarArg,
        Class<?> clazz) {
      return getPlayers().stream().map(OfflinePlayer::getName)
          .collect(Collectors.toUnmodifiableList());
    }

    protected abstract Collection<? extends OfflinePlayer> getPlayers();
  }

  private static final class OnlinePlayerDeserializer extends PlayerDeserializer<Player> {

    @Override
    public boolean varargIsValid(String[] args, Class<?> clazz) {
      if (super.varargIsValid(args, clazz)) {
        return Arrays.stream(args).allMatch(name -> Bukkit.getPlayer(name) != null);
      }
      return false;
    }

    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      if (super.isValid(input, clazz)) {
        return Bukkit.getPlayer(input) != null;
      }
      return false;
    }

    @Override
    public @NotNull Player deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      return Objects.requireNonNull(Bukkit.getPlayer(input));
    }

    @Override
    public Player[] deserializeVararg(String[] args, @NotNull Class<?> clazz) {
      return Arrays.stream(args).map(Bukkit::getPlayer).toArray(Player[]::new);
    }

    @Override
    protected Collection<? extends OfflinePlayer> getPlayers() {
      return Bukkit.getOnlinePlayers();
    }
  }

  private static final class OfflinePlayerDeserializer extends PlayerDeserializer<OfflinePlayer> {

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull OfflinePlayer deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      return Bukkit.getOfflinePlayer(input);
    }

    @SuppressWarnings("deprecation")
    @Override
    public OfflinePlayer[] deserializeVararg(String[] args, @NotNull Class<?> clazz) {
      return Arrays.stream(args).map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new);
    }

    @Override
    protected Collection<? extends OfflinePlayer> getPlayers() {
      return List.of(Bukkit.getOfflinePlayers());
    }
  }

  private static final class NumberDeserializer<T> implements ArgumentDeserializer<T> {

    private final Function<String, T> parser;
    private final IntFunction<T[]> arrayGenerator;
    private T output;
    private T[] varArgOutput;

    private NumberDeserializer(Function<String, T> parser,
        IntFunction<T[]> arrayGenerator) {
      this.parser = parser;
      this.arrayGenerator = arrayGenerator;
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(String[] args, Class<?> clazz) {
      try {
        varArgOutput = Arrays.stream(args).map(parser).toArray(arrayGenerator);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      try {
        output = parser.apply(input);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public @NotNull T deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      if (output == null) {
        throw new IllegalStateException(
            "This method is not supposed to be called when isValid returned false");
      }
      return output;
    }

    @Override
    public T[] deserializeVararg(String[] args, @NotNull Class<?> clazz) {
      if (varArgOutput == null) {
        throw new IllegalStateException(
            "This method is not supposed to be called when isValid returned false");
      }
      return varArgOutput;
    }
  }


  private static class EnumDeserializer<T extends Enum<T>> implements ArgumentDeserializer<T> {

    private final IntFunction<T[]> arrayGenerator;

    private EnumDeserializer(IntFunction<T[]> arrayGenerator) {
      this.arrayGenerator = arrayGenerator;
    }

    @Override
    public boolean isValid(@NotNull String input, @NotNull Class<?> clazz) {
      return Arrays.stream(clazz.getEnumConstants()).anyMatch(it -> it.toString().equals(input));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      var klass = (Class<T>) clazz;
      return Enum.valueOf(klass, input);
    }

    @Override
    public boolean supportsVararg() {
      return true;
    }

    @Override
    public boolean varargIsValid(String[] args, Class<?> clazz) {
      return Arrays.stream(args).allMatch(input -> Arrays.stream(clazz.getEnumConstants())
          .anyMatch(it -> it.toString().equals(input)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] deserializeVararg(String[] args, @NotNull Class<?> clazz) {
      return Arrays.stream(args).map(arg -> {
        var klass = (Class<T>) clazz;
        return Enum.valueOf(klass, arg);
      }).toArray(arrayGenerator);
    }

    @Override
    public @NotNull List<String> providePossibilities(@NotNull String parameterName, boolean isVarArg,
        Class<?> clazz) {
      return Arrays.stream(clazz.getEnumConstants()).map(Object::toString)
          .collect(Collectors.toUnmodifiableList());
    }
  }
}
