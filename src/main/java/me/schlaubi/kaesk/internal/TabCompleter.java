package me.schlaubi.kaesk.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.internal.CommandUtils.CommandContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TabCompleter {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;

  public TabCompleter(
      final Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    this.deserializers = deserializers;
  }

  @SuppressWarnings("unused")
    /* package-private */ List<String> onTabComplete(final CompiledCommandClass compiledCommand,
      final CommandSender sender,
      final Command command, final String alias, final String[] args) {
    final List<String> possibleArgs = findArgs(compiledCommand, args, sender);
    return possibleArgs.stream()
        .filter(it -> it.toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
        .collect(Collectors.toList());
  }

  private List<String> findArgs(CompiledCommandClass compiledCommand,
      String[] args, CommandSender sender) {
    final CommandContainer commandContainer = CommandUtils
        .findCommandInvokable(compiledCommand, args);
    final CommandInvokable invoke = commandContainer.getInvoke();
    if (!sender.hasPermission(invoke.getPermission())) {
      return Collections.emptyList();
    }
    CommandParameter currentParameter = findCurrentParameter(invoke, args.length);

    if (currentParameter == null) {
      final CommandParameter secondTry = findCurrentParameter(invoke, args.length + 1);
      if (secondTry == null) {
        return Collections.emptyList();
      }
      currentParameter = secondTry;
    }

    final Class<?> currentType = currentParameter.getType();
    final ArgumentDeserializer<?> converter = CommandUtils
        .findDeserializer(deserializers, currentParameter);
    List<String> possible = converter
        .providePossibilities(currentParameter.getName(), currentParameter.isVarArg(), currentType);
    if (args.length >= 1) {
      final List<String> list = new ArrayList<>(possible);
      list.addAll(commandContainer.getTreeElement().getChildren().entrySet().stream()
          .filter(it -> it.getValue().getLevel() == args.length
              && it.getValue().getInvokables().stream()
              .anyMatch(child -> child.getPermission().isEmpty() || sender
                  .hasPermission(child.getPermission()))).map(Map.Entry::getKey)
          .collect(Collectors.toList()));
      possible = Collections.unmodifiableList(list);
    }
    return possible;
  }

  private CommandParameter findCurrentParameter(final CommandInvokable invoke,
      final int argsLength) {
    final List<CommandParameter> parameters = invoke.getParameters();
    if (argsLength > parameters.size()) {
      if (parameters.isEmpty()) {
        return null;
      }
      final CommandParameter parameter = parameters.get(parameters.size() - 1);
      if (!parameter.isVarArg()) {
        return null;
      }
      return parameter;
    } else {
      return parameters.get(argsLength - 1);
    }
  }
}
