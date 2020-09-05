package me.schlaubi.kaesk.api.bungee;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import me.schlaubi.kaesk.api.AbstractCommandClientBuilder;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.internal.BungeeCommandClientImpl;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * BungeeCord specific builder for {@link CommandClient}.
 */
public class BungeeCommandClientBuilder extends
    AbstractCommandClientBuilder<CommandSender, BungeeCommandClientBuilder> {

  private Plugin plugin;

  /**
   * Default constructor
   */
  public BungeeCommandClientBuilder() {
  }

  /**
   * Constructor that consumes a {@link Plugin}
   * @param plugin the {@link Plugin} to register commands
   */
  public BungeeCommandClientBuilder(Plugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Sets the {@link Plugin} instance that registers commands.
   *
   * @param plugin the {@link Plugin} instance.
   * @return the current builder
   */
  @NotNull
  public BungeeCommandClientBuilder setPlugin(@NotNull Plugin plugin) {
    Preconditions.checkNotNull(plugin, "Plugin may not be null");
    this.plugin = plugin;
    return this;
  }

  /**
   * Sets the {@link Plugin} instance that registers commands by the plugins name.
   *
   * @param name the name of the plugin.
   * @return the current builder
   * @see #setPlugin(Plugin)
   * @see net.md_5.bungee.api.plugin.PluginManager#getPlugin(String)
   */
  @NotNull
  public BungeeCommandClientBuilder setPlugin(@NotNull String name) {
    Preconditions.checkNotNull(plugin, "Plugin name may not be null");
    return setPlugin(ProxyServer.getInstance().getPluginManager().getPlugin(name));
  }

  /**
   * @see AbstractCommandClientBuilder#build()
   */
  @Override
  public @NotNull BungeeCommandClient build() {
    checkForNulls();
    Preconditions.checkNotNull(plugin, "Plugin may not be null");
    return new BungeeCommandClientImpl(deserializers, argumentHandler, noPermissionHandler, plugin);
  }

  @NotNull
  protected Map<Class<?>, ArgumentDeserializer<?>> allDeserializers() {
    Map<Class<?>, ArgumentDeserializer<?>> deserializerMap = new HashMap<>();
    deserializerMap.put(ProxiedPlayer.class, BungeeConverters.PROXIED_PLAYER);
    return deserializerMap;
  }
}
