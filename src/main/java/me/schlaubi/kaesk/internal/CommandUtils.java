package me.schlaubi.kaesk.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class CommandUtils {

  @NotNull
  public static CommandContainer findCommandInvokable(@NotNull CompiledCommandClass compiledCommand,
      @NotNull String[] args) {
    var invoke = args.length > 0 ? args[0] : "root";
    return findCommandInvokable(invoke, args, compiledCommand.commandTree(),
        compiledCommand.commandTree());
  }

  @NotNull
  private static CommandContainer findCommandInvokable(@NotNull String invoke,
      @NotNull String[] args,
      @NotNull CommandTreeElement parent, @NotNull CommandTreeElement alternative) {
    var child = parent.findChild(invoke);
    if (child == null) {
      return new CommandContainer(args, findCommandInvokable(alternative, args),
          alternative);
    }
    var newArgs = Arrays.copyOfRange(args, 1, args.length);
    if (newArgs.length > 0) {
      var newInvoke = newArgs[0];
      return findCommandInvokable(newInvoke, newArgs, child, child);
    }
    return new CommandContainer(args, findCommandInvokable(alternative, newArgs),
        alternative);
  }

  @NotNull
  private static CommandInvokable findCommandInvokable(@NotNull CommandTreeElement element,
      @NotNull String[] args) {
    return element.getInvokables().stream().filter(invokable -> {
      List<CommandParameter> parameters = invokable.parameters();
      return parameters.size() == args.length || (!parameters.isEmpty()
          && parameters.get(parameters.size() - 1).isVarArg());
    }).findFirst().orElseGet(() -> element.getInvokables().stream()
        .max(Comparator.comparingInt(invokable -> invokable.parameters().size()))
        .orElseThrow(() -> new IllegalStateException("No command could be found!")));
  }

  static class CommandContainer {

    private final String[] parsedArgs;
    private final CommandInvokable invoke;
    private final CommandTreeElement treeElement;

    public CommandContainer(String[] parsedArgs, CommandInvokable invoke,
        CommandTreeElement treeElement) {
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
