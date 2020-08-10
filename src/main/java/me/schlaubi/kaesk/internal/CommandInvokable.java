package me.schlaubi.kaesk.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class CommandInvokable {
  @NotNull
  private final Object executor;
  @NotNull
  private final Method invokable;
  @NotNull
  private final String name;
  @NotNull
  private final List<CommandParameter>parameters;
  @NotNull
  private final String permission;
  private final boolean consoleAllowed;

  public CommandInvokable(@NotNull Object executor,
      @NotNull Method invokable, @NotNull String name,
      @NotNull List<CommandParameter> parameters,
      @NotNull String permission, boolean consoleAllowed) {
    this.executor = executor;
    this.invokable = invokable;
    this.name = name;
    this.parameters = parameters;
    this.permission = permission;
    this.consoleAllowed = consoleAllowed;
  }

  public void invoke(@NotNull final Object... args) throws InvocationTargetException, IllegalAccessException {
    invokable.invoke(executor, args);
  }

  public @NotNull Object getExecutor() {
    return executor;
  }

  public @NotNull Method getInvokable() {
    return invokable;
  }

  public @NotNull String getName() {
    return name;
  }

  public @NotNull List<CommandParameter> getParameters() {
    return parameters;
  }

  public @NotNull String getPermission() {
    return permission;
  }

  public boolean isConsoleAllowed() {
    return consoleAllowed;
  }
}
