package me.schlaubi.kaesk.api.bungee;

import java.util.Arrays;
import java.util.Collection;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.converters.Converters;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Bungee specific deserializers.
 */
public class BungeeConverters {

  /**
   * Deserializer for {@link ProxiedPlayer}.
   */
  public static final ArgumentDeserializer<ProxiedPlayer> PROXIED_PLAYER = new PlayerDeserializer();

  private static final class PlayerDeserializer extends
      Converters.PlayerDeserializer<ProxiedPlayer> {

    protected PlayerDeserializer() {
      super(ProxiedPlayer::getDisplayName);
    }

    @Override
    protected Collection<? extends ProxiedPlayer> getPlayers() {
      return ProxyServer.getInstance().getPlayers();
    }

    @NotNull
    @Override
    public ProxiedPlayer deserialize(@NotNull String input, @NotNull Class<?> clazz) {
      return ProxyServer.getInstance().getPlayer(input);
    }

    @NotNull
    @Override
    public ProxiedPlayer[] deserializeVararg(@NotNull String[] args,
        @NotNull Class<?> clazz) {
      return Arrays.stream(args).map(ProxyServer.getInstance()::getPlayer)
          .toArray(ProxiedPlayer[]::new);
    }
  }
}
