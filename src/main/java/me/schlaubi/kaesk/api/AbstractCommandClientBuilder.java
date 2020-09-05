package me.schlaubi.kaesk.api;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import me.schlaubi.kaesk.api.converters.Converters;
import org.jetbrains.annotations.NotNull;

/**
 * Base for builders for {@link CommandClient}.
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class AbstractCommandClientBuilder<SENDER, BUILDER extends AbstractCommandClientBuilder<SENDER, BUILDER>> {

  protected Map<Class<?>, ArgumentDeserializer<?>> deserializers = _allDeserializers();
  protected InvalidArgumentHandler argumentHandler;
  protected NoPermissionHandler noPermissionHandler;

  /**
   * Adds a new {@link ArgumentDeserializer}.
   *
   * @param clazz the {@link Class} of the type the deserializer is handling.
   * @param deserializer the {@link ArgumentDeserializer}
   * @param <T> the type
   * @return the builder
   */
  @NotNull
  public <T> BUILDER addDeserializer(@NotNull final Class<T> clazz,
      final ArgumentDeserializer<T> deserializer) {
    Preconditions.checkNotNull(clazz, "Class may not be null");
    Preconditions.checkNotNull(deserializer, "Deserializer may not be null");
    deserializers.put(clazz, deserializer);
    return (BUILDER) this;
  }

  /**
   * Sets all {@link ArgumentDeserializer}. {@literal This will unregister all default
   * deserializer}
   *
   * @param deserializers the deserializers
   * @return the builder
   */
  @NotNull
  public BUILDER setDeserializers(
      @NotNull final Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    Preconditions.checkNotNull(deserializers, "Deserializers may not be null");
    this.deserializers = deserializers;
    return (BUILDER) this;
  }


  /**
   * Sets the {@link InvalidArgumentHandler}.
   *
   * @param argumentHandler the handler
   * @return the builder
   * @see InvalidArgumentHandler
   */
  @NotNull
  public BUILDER setArgumentHandler(@NotNull final InvalidArgumentHandler argumentHandler) {
    Preconditions.checkNotNull(argumentHandler, "Argument handler may not be null");
    this.argumentHandler = argumentHandler;
    return (BUILDER) this;
  }

  /**
   * Sets the {@link NoPermissionHandler}.
   *
   * @param noPermissionHandler the handler
   * @return the builder
   * @see NoPermissionHandler
   */
  @NotNull
  public BUILDER setNoPermissionHandler(final NoPermissionHandler noPermissionHandler) {
    this.noPermissionHandler = noPermissionHandler;
    return (BUILDER) this;
  }

  /**
   * Builds the {@link CommandClient}.
   *
   * @return the command client
   */
  @NotNull
  public abstract CommandClient<SENDER> build();

  protected void checkForNulls() {
    Preconditions.checkNotNull(argumentHandler, "Argument handler may not be null");
    Preconditions.checkNotNull(noPermissionHandler, "Permission handler may not be null");
  }

  @NotNull
  protected abstract Map<Class<?>, ArgumentDeserializer<?>> allDeserializers();

  private Map<Class<?>, ArgumentDeserializer<?>> _allDeserializers() {
    Map<Class<?>, ArgumentDeserializer<?>> deserializerMap = new HashMap<>();
    deserializerMap.put(Integer.class, Converters.INTEGER);
    deserializerMap.put(Float.class, Converters.FLOAT);
    deserializerMap.put(Double.class, Converters.DOUBLE);
    deserializerMap.put(Short.class, Converters.SHORT);
    deserializerMap.put(String.class, Converters.STRING);
    deserializerMap.putAll(allDeserializers());
    return deserializerMap;
  }
}
