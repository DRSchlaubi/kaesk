package me.schlaubi.kaesk.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.NoPermissionHandler;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.ArgumentException;
import me.schlaubi.kaesk.api.InvalidArgumentHandler;
import me.schlaubi.kaesk.internal.CommandUtils.CommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class CommandExecutor {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;
  private final InvalidArgumentHandler argumentHandler;
  private NoPermissionHandler noPermissionHandler;

  public CommandExecutor(
      @NotNull Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      @NotNull InvalidArgumentHandler argumentHandler,
      NoPermissionHandler noPermissionHandler) {
    this.deserializers = deserializers;
    this.argumentHandler = argumentHandler;
    this.noPermissionHandler = noPermissionHandler;
  }

  @SuppressWarnings("unused")
    /* package-private */ boolean onCommand(@NotNull CompiledCommandClass compiledCommand,
      @NotNull CommandSender sender,
      @NotNull Command command, @NotNull String label, @NotNull final String[] args) {
    CommandContainer commandContainer = CommandUtils.findCommandInvokable(compiledCommand, args);
    CommandInvokable invoke = commandContainer.getInvoke();

    if (!invoke.consoleAllowed() && sender instanceof ConsoleCommandSender) {
      sender.sendMessage("This command is not allowed in console");
      return true;
    }

    var permission = invoke.permission();
    if (!permission.isBlank() && !sender.hasPermission(permission)) {
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
    if (invoke.consoleAllowed()) {
      convertedArgs.add(0, sender);
    } else {
      Player player = (Player) sender;
      convertedArgs.add(0, player);
    }
    try {
      invoke.invoke(convertedArgs.toArray());
      return true;
    } catch (InvocationTargetException | IllegalAccessException e) {
      Bukkit.getLogger().log(Level.SEVERE, "Error while invoking method", e);
      return false;
    }
  }

  @NotNull
  private List<Object> convertArgs(@NotNull String[] _args, @NotNull CommandInvokable invokable) {
    int parameterSize = invokable.parameters().size();
    if (parameterSize == 0) {
      _args = new String[0];
    }
    if (_args.length > parameterSize && !invokable.parameters().get(parameterSize - 1).isVarArg()) {
      _args = Arrays.copyOfRange(_args, 0, parameterSize);
    }
    final String[] args = _args;
    return invokable.parameters().stream().map(parameter -> {
      var converter = deserializers.get(parameter.type());
      if (parameter.isVarArg()) {
        var input = Arrays
            .copyOfRange(args, invokable.parameters().indexOf(parameter), args.length);
        if (!converter.varargIsValid(input, parameter.type())) {
          throw new ArgumentException("Invalid parameter input ", converter, parameter.type());
        }
        return converter.deserializeVararg(input, parameter.type());
      } else {
        var input = args[invokable.parameters().indexOf(parameter)];
        if (!converter.isValid(input, parameter.type())) {
          throw new ArgumentException("Invalid parameter input ", converter, parameter.type());
        }
        return converter.deserialize(input, parameter.type());
      }
    }).collect(Collectors.toList());
  }
}
