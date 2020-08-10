package me.schlaubi.kaesk.api;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import me.schlaubi.kaesk.api.converters.Converters;
import me.schlaubi.kaesk.internal.DefaultCommandClient;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link CommandClient}.
 */
@SuppressWarnings("unused")
public final class CommandClientBuilder {

  private Map<Class<?>, ArgumentDeserializer<?>> deserializers = allDeserializers();
  private JavaPlugin plugin;
  private InvalidArgumentHandler argumentHandler;
  private NoPermissionHandler noPermissionHandler;

  /**
   * Default constructor.
   */
  public CommandClientBuilder() {
  }

  /**
   * Constructor consuming {@link JavaPlugin}.
   *
   * @param plugin the {@link JavaPlugin} that is registering commands.
   */
  public CommandClientBuilder(@NotNull final JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Constructor consuming {@link JavaPlugin} and {@link InvalidArgumentHandler}.
   *
   * @param plugin the {@link JavaPlugin} that is registering commands.
   * @param argumentHandler the {@link InvalidArgumentHandler} that handles invalid argument input.
   */
  @NotNull
  public CommandClientBuilder(@NotNull final JavaPlugin plugin,
      @NotNull final InvalidArgumentHandler argumentHandler) {
    this.plugin = plugin;
    this.argumentHandler = argumentHandler;
  }

  /**
   * Adds a new {@link ArgumentDeserializer}.
   *
   * @param clazz the {@link Class} of the type the deserializer is handling.
   * @param deserializer the {@link ArgumentDeserializer}
   * @param <T> the type
   * @return the builder
   */
  @NotNull
  public <T> CommandClientBuilder addDeserializer(@NotNull final Class<T> clazz,
      final ArgumentDeserializer<T> deserializer) {
    Preconditions.checkNotNull(clazz, "Class may not be null");
    Preconditions.checkNotNull(deserializer, "Deserializer may not be null");
    deserializers.put(clazz, deserializer);
    return this;
  }

  /**
   * Sets all {@link ArgumentDeserializer}. {@literal This will unregister all default
   * deserializer}
   *
   * @param deserializers the deserializers
   * @return the builder
   */
  @NotNull
  public CommandClientBuilder setDeserializers(
      @NotNull final Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    Preconditions.checkNotNull(deserializers, "Deserializers may not be null");
    this.deserializers = deserializers;
    return this;
  }

  /**
   * Set's the {@link JavaPlugin} instance.
   *
   * @param plugin the plugin
   * @return the builder
   */
  @NotNull
  public CommandClientBuilder setPlugin(@NotNull final JavaPlugin plugin) {
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
  public CommandClientBuilder setPlugin(@NotNull final Class<? extends JavaPlugin> clazz) {
    Preconditions.checkNotNull(clazz, "Clazz may not be null");
    return setPlugin(JavaPlugin.getPlugin(clazz));
  }

  /**
   * Sets the {@link InvalidArgumentHandler}.
   *
   * @param argumentHandler the handler
   * @return the builder
   * @see InvalidArgumentHandler
   */
  @NotNull
  public CommandClientBuilder setArgumentHandler(@NotNull final InvalidArgumentHandler argumentHandler) {
    Preconditions.checkNotNull(argumentHandler, "Argument handler may not be null");
    this.argumentHandler = argumentHandler;
    return this;
  }

  /**
   * Sets the {@link NoPermissionHandler}.
   *
   * @param noPermissionHandler the handler
   * @return the builder
   * @see NoPermissionHandler
   */
  @NotNull
  public CommandClientBuilder setNoPermissionHandler(final NoPermissionHandler noPermissionHandler) {
    this.noPermissionHandler = noPermissionHandler;
    return this;
  }

  /**
   * Builds the {@link CommandClient}.
   *
   * @return the command client
   */
  @NotNull
  public CommandClient build() {
    Preconditions.checkNotNull(plugin, "Plugin may not be null");
    Preconditions.checkNotNull(argumentHandler, "Argument handler may not be null");
    Preconditions.checkNotNull(noPermissionHandler, "Permission handler may not be null");
    return new DefaultCommandClient(deserializers, plugin, argumentHandler, noPermissionHandler);
  }

  @NotNull
  private static Map<Class<?>, ArgumentDeserializer<?>> allDeserializers() {
    Map<Class<?>, ArgumentDeserializer<?>> deserializerMap = new HashMap<>();
    deserializerMap.put(Integer.class, Converters.INTEGER);
    deserializerMap.put(Float.class, Converters.FLOAT);
    deserializerMap.put(Double.class, Converters.DOUBLE);
    deserializerMap.put(Short.class, Converters.SHORT);
    deserializerMap.put(OfflinePlayer.class, Converters.OFFLINE_PLAYER);
    deserializerMap.put(Player.class, Converters.PLAYER);
    deserializerMap.put(String.class, Converters.STRING);
    deserializerMap.put(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new));
    deserializerMap.put(Material.class, Converters.newEnumDeserializer(Material[]::new));
    return deserializerMap;
  }

}
