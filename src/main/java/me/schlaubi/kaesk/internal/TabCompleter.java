package me.schlaubi.kaesk.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TabCompleter {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;

  public TabCompleter(
      Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    this.deserializers = deserializers;
  }

  @SuppressWarnings("unused")
    /* package-private */ List<String> onTabComplete(CompiledCommandClass compiledCommand,
      CommandSender sender,
      Command command, String alias, String[] args) {
    List<String> possibleArgs = findArgs(compiledCommand, args);
    return possibleArgs.stream()
        .filter(it -> it.toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
        .collect(Collectors.toUnmodifiableList());
  }

  private List<String> findArgs(CompiledCommandClass compiledCommand,
      String[] args) {
    var commandContainer = CommandUtils.findCommandInvokable(compiledCommand, args);
    var invoke = commandContainer.getInvoke();
    CommandParameter currentParameter = findCurrentParameter(invoke, args.length);

    if (currentParameter == null) {
      var secondTry = findCurrentParameter(invoke, args.length + 1);
      if (secondTry == null) {
        return Collections.emptyList();
      }
      currentParameter = secondTry;
    }

    var currentType = currentParameter.type();
    var converter = deserializers.get(currentType);
    var possible = converter
        .providePossibilities(currentParameter.name(), currentParameter.isVarArg(), currentType);
    if (args.length >= 1) {
      var list = new ArrayList<>(possible);
      list.addAll(commandContainer.getTreeElement().getChildren().entrySet().stream()
          .filter(it -> it.getValue().getLevel() == args.length).map(Map.Entry::getKey)
          .collect(Collectors.toUnmodifiableList()));
      possible = Collections.unmodifiableList(list);
    }
    return possible;
  }

  private CommandParameter findCurrentParameter(CommandInvokable invoke, int argsLength) {
    var parameters = invoke.parameters();
    if (argsLength > parameters.size()) {
      if (parameters.isEmpty()) {
        return null;
      }
      var parameter = parameters.get(parameters.size() - 1);
      if (!parameter.isVarArg()) {
        return null;
      }
      return parameter;
    } else {
      return parameters.get(argsLength - 1);
    }
  }

}
