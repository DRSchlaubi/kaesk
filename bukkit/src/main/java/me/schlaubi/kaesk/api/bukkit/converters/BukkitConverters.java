package me.schlaubi.kaesk.api.bukkit.converters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.converters.Converters.PlayerDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit specific converters
 */
public class
BukkitConverters {

  /**
   * Converter for Player.
   */
  public static final ArgumentDeserializer<Player> PLAYER = new OnlinePlayerDeserializer();

  /**
   * Converter for OfflinePlayer.
   */
  public static final ArgumentDeserializer<OfflinePlayer> OFFLINE_PLAYER = new OfflinePlayerDeserializer();


  private static final class OnlinePlayerDeserializer extends PlayerDeserializer<Player> {

    protected OnlinePlayerDeserializer() {
      super(OfflinePlayer::getName);
    }

    @Override
    public boolean varargIsValid(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      if (super.varargIsValid(args, clazz)) {
        return Arrays.stream(args).allMatch(name -> Bukkit.getPlayer(name) != null);
      }
      return false;
    }

    @Override
    public boolean isValid(@NotNull final String input, @NotNull final Class<?> clazz) {
      if (super.isValid(input, clazz)) {
        return Bukkit.getPlayer(input) != null;
      }
      return false;
    }

    @Override
    public @NotNull Player deserialize(@NotNull final String input, @NotNull final Class<?> clazz) {
      return Objects.requireNonNull(Bukkit.getPlayer(input));
    }

    @Override
    public Player[] deserializeVararg(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      return Arrays.stream(args).map(Bukkit::getPlayer).toArray(Player[]::new);
    }

    @Override
    public @NotNull List<String> providePossibilities(@NotNull String parameterName,
        boolean isVarArg, Class<?> clazz) {
      return Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    @Override
    protected Collection<? extends Player> getPlayers() {
      return Bukkit.getOnlinePlayers();
    }
  }

  private static final class OfflinePlayerDeserializer extends PlayerDeserializer<OfflinePlayer> {

    private OfflinePlayerDeserializer() {
      super(OfflinePlayer::getName);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull OfflinePlayer deserialize(@NotNull final String input, @NotNull final Class<?> clazz) {
      return Bukkit.getOfflinePlayer(input);
    }

    @SuppressWarnings("deprecation")
    @Override
    public OfflinePlayer[] deserializeVararg(@NotNull final String[] args, @NotNull final Class<?> clazz) {
      return Arrays.stream(args).map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new);
    }

    @Override
    protected Collection<? extends OfflinePlayer> getPlayers() {
      return Collections.unmodifiableCollection(Arrays.asList(Bukkit.getOfflinePlayers()));
    }
  }
}
