package me.schlaubi.kaesk.internal;

import me.schlaubi.kaesk.api.ArgumentDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CommandParameter {

  @NotNull
  private final Class<?> type;
  private final boolean isVarArg;
  @NotNull
  private final String name;
  @Nullable
  private final Class<? extends ArgumentDeserializer<?>> deserializer;

  CommandParameter(@NotNull Class<?> type, boolean isVarArg, @NotNull String name,
      @Nullable Class<? extends ArgumentDeserializer<?>> deserializer) {
    this.type = type;
    this.isVarArg = isVarArg;
    this.name = name;
    this.deserializer = deserializer;
  }

  public @NotNull Class<?> getType() {
    return type;
  }

  public boolean isVarArg() {
    return isVarArg;
  }

  public @NotNull String getName() {
    return name;
  }

  public @Nullable Class<? extends ArgumentDeserializer<?>> getDeserializer() {
    return deserializer;
  }
}
