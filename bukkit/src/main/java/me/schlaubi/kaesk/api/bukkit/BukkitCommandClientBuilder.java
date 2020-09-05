package me.schlaubi.kaesk.api.bukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import me.schlaubi.kaesk.api.AbstractCommandClientBuilder;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.CommandClient;
import me.schlaubi.kaesk.api.bukkit.converters.BukkitConverters;
import me.schlaubi.kaesk.api.converters.Converters;
import me.schlaubi.kaesk.internal.BukkitCommandClientImpl;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for bukkit specific {@link CommandClient}.
 */
public class BukkitCommandClientBuilder extends AbstractCommandClientBuilder<CommandSender, BukkitCommandClientBuilder> {

  private JavaPlugin plugin;

  /**
   * Default constructor
   */
  public BukkitCommandClientBuilder() {
  }

  /**
   * Constructor consuming {@link JavaPlugin}.
   *
   * @param plugin the {@link JavaPlugin} that is registering commands.
   */
  public BukkitCommandClientBuilder(@NotNull final JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Set's the {@link JavaPlugin} instance.
   *
   * @param plugin the plugin
   * @return the builder
   */
  @NotNull
  public BukkitCommandClientBuilder setPlugin(@NotNull final JavaPlugin plugin) {
    Preconditions.checkNotNull(plugin, "Plugin may not be null");
    this.plugin = plugin;
    return this;
  }


  /**
   * Sets the {@link JavaPlugin} by its {@link Class} instance.
   *
   * @param clazz the {@link Class} of the plugin
   * @return the builder
   * @see #setPlugin(JavaPlugin)
   */
  @NotNull
  public BukkitCommandClientBuilder setPlugin(@NotNull final Class<? extends JavaPlugin> clazz) {
    Preconditions.checkNotNull(clazz, "Clazz may not be null");
    return setPlugin(JavaPlugin.getPlugin(clazz));
  }

  /**
   * @inheritDoc
   */
  @Override
  public @NotNull BukkitCommandClient build() {
    checkForNulls();
    Preconditions.checkNotNull(plugin, "Plugin may not be null");
    return new BukkitCommandClientImpl(deserializers, plugin, argumentHandler, noPermissionHandler);
  }

  @NotNull
  protected Map<Class<?>, ArgumentDeserializer<?>> allDeserializers() {
    Map<Class<?>, ArgumentDeserializer<?>> deserializerMap = new HashMap<>();
    deserializerMap.put(OfflinePlayer.class, BukkitConverters.OFFLINE_PLAYER);
    deserializerMap.put(Player.class, BukkitConverters.PLAYER);
    deserializerMap.put(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new));
    deserializerMap.put(Material.class, Converters.newEnumDeserializer(Material[]::new));
    return deserializerMap;
  }
}
