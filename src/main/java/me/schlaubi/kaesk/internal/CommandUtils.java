package me.schlaubi.kaesk.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import org.jetbrains.annotations.NotNull;

class CommandUtils {

  @SuppressWarnings("unchecked")
  @NotNull
  public static <T> ArgumentDeserializer<T> findDeserializer(
      @NotNull Map<Class<?>, ArgumentDeserializer<?>> deserializers,
      @NotNull final CommandParameter parameter) {
    if (parameter.getDeserializer() != null) {
      return (ArgumentDeserializer<T>) deserializers.get(parameter.getDeserializer());
    } else if (deserializers.containsKey(parameter.getType())) {
      return (ArgumentDeserializer<T>) deserializers.get(parameter.getType());
    } else {
      throw new IllegalArgumentException(
          String.format("Cannot find deserializer for type %s", parameter.getType()));
    }
  }

  @NotNull
  public static CommandContainer findCommandInvokable(
      @NotNull final CompiledCommandClass compiledCommand,
      @NotNull final String[] args) {
    final String invoke = args.length > 0 ? args[0] : "root";
    return findCommandInvokable(invoke, args, compiledCommand.getCommandTree(),
        compiledCommand.getCommandTree());
  }

  @NotNull
  private static CommandContainer findCommandInvokable(@NotNull final String invoke,
      @NotNull final String[] args,
      @NotNull final CommandTreeElement parent, @NotNull final CommandTreeElement alternative) {
    final CommandTreeElement child = parent.findChild(invoke);
    if (child == null) {
      return new CommandContainer(args, findCommandInvokable(alternative, args),
          alternative);
    }
    final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
    if (newArgs.length > 0) {
      final String newInvoke = newArgs[0];
      return findCommandInvokable(newInvoke, newArgs, child, child);
    }
    return new CommandContainer(args, findCommandInvokable(alternative, newArgs),
        alternative);
  }

  @NotNull
  private static CommandInvokable findCommandInvokable(@NotNull final CommandTreeElement element,
      @NotNull final String[] args) {
    return element.getInvokables().stream().filter(invokable -> {
      final List<CommandParameter> parameters = invokable.getParameters();
      return parameters.size() == args.length || (!parameters.isEmpty()
          && parameters.get(parameters.size() - 1).isVarArg());
    }).findFirst().orElseGet(() -> element.getInvokables().stream()
        .max(Comparator.comparingInt(invokable -> invokable.getParameters().size()))
        .orElseThrow(() -> new IllegalStateException("No command could be found!")));
  }

  static class CommandContainer {

    private final String[] parsedArgs;
    private final CommandInvokable invoke;
    private final CommandTreeElement treeElement;

    public CommandContainer(final String[] parsedArgs, final CommandInvokable invoke,
        final CommandTreeElement treeElement) {
      this.parsedArgs = parsedArgs;
      this.invoke = invoke;
      this.treeElement = treeElement;
    }

    @NotNull
    public CommandInvokable getInvoke() {
      return invoke;
    }

    @NotNull
    public String[] getParsedArgs() {
      return parsedArgs;
    }

    @NotNull
    public CommandTreeElement getTreeElement() {
      return treeElement;
    }
  }
}
