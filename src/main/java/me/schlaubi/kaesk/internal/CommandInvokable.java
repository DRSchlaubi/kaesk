package me.schlaubi.kaesk.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.jetbrains.annotations.NotNull;

record CommandInvokable(@NotNull Object executor, @NotNull Method invokable,
                        @NotNull String name,
                        @NotNull List<CommandParameter>parameters,
                        @NotNull String permission,
                        boolean consoleAllowed
) {

  public void invoke(@NotNull Object... args) throws InvocationTargetException, IllegalAccessException {
    invokable.invoke(executor, args);
  }
}
