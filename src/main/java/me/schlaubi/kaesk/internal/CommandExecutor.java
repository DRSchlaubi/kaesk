package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.ArgumentException;
import me.schlaubi.kaesk.api.CommandSender;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import me.schlaubi.kaesk.internal.CommandUtils.CommandContainer;
import org.jetbrains.annotations.NotNull;

class CommandExecutor<PLAYER> {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;
  private final InvalidArgumentHandler argumentHandler;
  private final NoPermissionHandler noPermissionHandler;
  private final Class<PLAYER> playerClazz;
  private final Logger logger;

  public CommandExecutor(
      @NotNull final Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      @NotNull final InvalidArgumentHandler argumentHandler,
      @NotNull final NoPermissionHandler noPermissionHandler,
      @NotNull Class<PLAYER> playerClazz,
      @NotNull Logger logger) {
    this.deserializers = deserializers;
    this.argumentHandler = argumentHandler;
    this.noPermissionHandler = noPermissionHandler;
    this.playerClazz = playerClazz;
    this.logger = logger;
  }

  @SuppressWarnings("unused")
    /* package-private */ boolean onCommand(@NotNull final CompiledCommandClass compiledCommand,
      @NotNull final CommandSender<?> sender, @NotNull final String label, @NotNull final String[] args) {
    final CommandContainer commandContainer = CommandUtils
        .findCommandInvokable(compiledCommand, args);
    final CommandInvokable invoke = commandContainer.getInvoke();

    if (!invoke.isConsoleAllowed() && sender.isConsole()) {
      sender.sendMessage("This command is not allowed in console");
      return true;
    }

    final String permission = invoke.getPermission();
    if (!permission.isEmpty() && !sender.hasPermission(permission)) {
      noPermissionHandler.handleNoPermissions(sender, permission);
      return true;
    }

    List<Object> convertedArgs;
    try {
      convertedArgs = convertArgs(commandContainer.getParsedArgs(), invoke);
    } catch (ArgumentException e) {
      argumentHandler.handleInvalidArgument(e, sender);
      return true;
    }
    if (invoke.isConsoleAllowed()) {
      convertedArgs.add(0, sender.getActual());
    } else {
      final PLAYER player = playerClazz.cast(sender.getActual());
      convertedArgs.add(0, player);
    }
    try {
      invoke.invoke(convertedArgs.toArray());
      return true;
    } catch (InvocationTargetException | IllegalAccessException e) {
      logger.log(Level.SEVERE, "Error while invoking method", e);
      return false;
    }
  }

  @NotNull
  private List<Object> convertArgs(@NotNull String[] _args,
      @NotNull final CommandInvokable invokable) {
    final int parameterSize = invokable.getParameters().size();
    if (parameterSize == 0) {
      _args = new String[0];
    }
    if (_args.length > parameterSize && !invokable.getParameters().get(parameterSize - 1).isVarArg()) {
      _args = Arrays.copyOfRange(_args, 0, parameterSize);
    }
    final String[] args = _args;
    return invokable.getParameters().stream().map(parameter -> {
      Preconditions.checkArgument(args.length > 0, "If you wish to have a default command without any parameters please add one to your command class");
      final ArgumentDeserializer<?> converter = CommandUtils.findDeserializer(deserializers, parameter);
      if (parameter.isVarArg()) {
        final String[] input = Arrays
            .copyOfRange(args, invokable.getParameters().indexOf(parameter), args.length);
        if (!converter.varargIsValid(input, parameter.getType())) {
          throw new ArgumentException("Invalid parameter input ", converter, parameter.getType());
        }
        return converter.deserializeVararg(input, parameter.getType());
      } else {
        final String input = args[invokable.getParameters().indexOf(parameter)];
        if (!converter.isValid(input, parameter.getType())) {
          throw new ArgumentException("Invalid parameter input ", converter, parameter.getType());
        }
        return converter.deserialize(input, parameter.getType());
      }
    }).collect(Collectors.toList());
  }
}
