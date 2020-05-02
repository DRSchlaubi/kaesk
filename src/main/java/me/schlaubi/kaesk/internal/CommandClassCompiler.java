package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import me.schlaubi.kaesk.api.CommandParents;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CommandClassCompiler {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;

  public CommandClassCompiler(
      @NotNull Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    this.deserializers = deserializers;
  }

  @NotNull
  public CompiledCommandClass compile(@NotNull Object executor) {
    var clazz = executor.getClass();
    Preconditions.checkArgument(clazz.isAnnotationPresent(CommandClass.class),
        "Class needs to be annotated with @CommandClass!");
    var commandClass = clazz.getDeclaredAnnotation(CommandClass.class);
    // Check for invokes in class
    var invokes = findInvokes(clazz);
    // Plant command tree
    var tree = plantCommandTree(invokes, clazz, commandClass, executor);
    return new CompiledCommandClass(tree, commandClass.name(), commandClass.permission());
  }

  @NotNull
  private CommandTreeElement plantCommandTree(@NotNull List<String[]> invokes,
      @NotNull Class<?> clazz,
      @NotNull CommandClass commandClass, @NotNull Object executor) {
    // Convert methods into invokables
    var methods = readMethods(clazz);
    // Get root invokables
    var root = methods.stream()
        .filter(method -> method.command().root()).map(method -> {
              var annotation = method.command();
              var permission = findPermission(annotation, commandClass);
              return new CommandInvokable(executor, method.method(), "root",
                  compileParameters(annotation, method.method()), permission,
                  annotation.consoleAllowed());
            }
        ).collect(Collectors.toUnmodifiableList());

    // Grow tree
    Preconditions.checkArgument(!root.isEmpty(), "You need to have at least one root command");
    return new CommandTreeElement(growCommandTree(invokes, executor, commandClass, methods, 1),
        root, 1);
  }

  @NotNull
  private Map<String, CommandTreeElement> growCommandTree(@NotNull List<String[]> invokes,
      @NotNull Object executor,
      @NotNull CommandClass commandClass,
      @NotNull List<PotentialCommand> methods, int level) {
    return findInvokable(invokes, executor, commandClass, methods, level).entrySet().stream()
        .map(entry -> new SimpleEntry<>(entry.getKey(),
            new CommandTreeElement(
                growCommandTree(invokes, executor, commandClass, methods, level + 1),
                entry.getValue(), level)))
        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
  }

  @NotNull
  private Map<String, List<CommandInvokable>> findInvokable(@NotNull List<String[]> invokes,
      @NotNull Object executor,
      @NotNull CommandClass commandClass,
      @NotNull List<PotentialCommand> methods, int level) {
    return invokes.stream().filter(invoke -> invoke.length == level)
        .map(invoke -> {
          var invokables = methods.stream()
              .filter(invokable -> Arrays.equals(findInvoke(invokable.method()), invoke))
              .map(invokable -> {
                var command = invokable.command();
                var permission = findPermission(command, commandClass);
                return new CommandInvokable(executor, invokable.method(), command.name(),
                    compileParameters(command, invokable.method()), permission,
                    command.consoleAllowed());
              })
              .collect(Collectors.toUnmodifiableList());
          return new SimpleEntry<>(invoke[invoke.length - 1],
              invokables);
        }).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
  }

  @NotNull
  private List<PotentialCommand> readMethods(@NotNull Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(Command.class)).map(method -> {
          Preconditions.checkArgument(Modifier.isPublic(method.getModifiers()),
              "Command method must be public! Method: ".formatted(method));
          CommandParents parents = findCommandParents(method);

          Command annotation = method.getAnnotation(Command.class);
          if (annotation.root()) {
            Preconditions.checkArgument(parents == null,
                "Root command cannot have parents! Method: ".formatted(method));
          }
          return new PotentialCommand(method, annotation, parents);
        }).collect(Collectors.toUnmodifiableList());
  }

  @Nullable
  private CommandParents findCommandParents(@NotNull Method method) {
    CommandParents parents = null;
    if (method.isAnnotationPresent(CommandParents.class)) {
      parents = method.getAnnotation(CommandParents.class);
    }
    return parents;
  }

  @NotNull
  private List<String[]> findInvokes(@NotNull Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(Command.class) && !method
            .getAnnotation(Command.class).root())
        .map(this::findInvoke).collect(Collectors.toUnmodifiableList());
  }

  @NotNull
  private String[] findInvoke(@NotNull Method method) {
    var parents = method.isAnnotationPresent(CommandParents.class) ?
        new ArrayList<>(Arrays.asList(method.getAnnotation(CommandParents.class).value()))
        : new ArrayList<String>();
    var annotation = method.getAnnotation(Command.class);
    parents.add(annotation.name());
    return parents.toArray(String[]::new);
  }

  @NotNull
  private List<CommandParameter> compileParameters(@NotNull Command invokable,
      @NotNull Method method) {
    var first = method.getParameters()[0];
    Preconditions.checkArgument(!invokable.consoleAllowed() || first.getType().getName().equals(
        CommandSender.class.getName()),
        "If console is allowed first argument has to be CommandSender. Method: %s"
            .formatted(method));
    return Arrays.stream(method.getParameters())
        .skip(1) // sender argument
        .map(parameter -> {

          Class<?> type = getParameterType(parameter);
          Preconditions.checkArgument(!type.isPrimitive(),
              "Primitive types are not supported please use wrapper types. Method: %s"
                  .formatted(method));
          Preconditions.checkArgument(deserializers.containsKey(type),
              "Unknown parameter %s. Method: %s".formatted(type, method));
          boolean isVarArg = parameter.isVarArgs();
          if (isVarArg) {
            Preconditions.checkState(deserializers.get(type).supportsVararg(),
                "Type %s does not support var args! Method: %s".formatted(type, method));
          }
          var data = findParameterData(parameter);
          var name = data.getKey();
          var surrounding = data.getValue();
          return new CommandParameter(type, isVarArg, name, surrounding);
        }).collect(Collectors.toUnmodifiableList());
  }

  @NotNull
  private AbstractMap.SimpleEntry<String, String> findParameterData(@NotNull Parameter parameter) {
    if (parameter.isAnnotationPresent(CommandArgument.class)) {
      var annotation = parameter.getAnnotation(CommandArgument.class);
      return new AbstractMap.SimpleEntry<>(annotation.name(), annotation.surrounded());
    } else {
      return new AbstractMap.SimpleEntry<>(parameter.getName(), "");
    }
  }

  @NotNull
  private Class<?> getParameterType(@NotNull Parameter parameter) {
    Class<?> type = parameter.getType();
    if (parameter.isVarArgs()) {
      return type.componentType();
    }
    return type;
  }

  @NotNull
  private String findPermission(@NotNull Command command, @NotNull CommandClass commandClass) {
    return
        command.permission().equals(Command.INHERIT_PERMISSION) ? commandClass.permission()
            : command.permission();
  }

  private record PotentialCommand(@NotNull Method method, @NotNull Command command,
                                  @Nullable CommandParents parents) {

  }
}
