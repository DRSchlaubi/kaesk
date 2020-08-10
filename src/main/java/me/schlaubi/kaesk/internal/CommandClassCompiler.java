package me.schlaubi.kaesk.internal;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.Command;
import me.schlaubi.kaesk.api.CommandArgument;
import me.schlaubi.kaesk.api.CommandClass;
import me.schlaubi.kaesk.api.CommandParents;
import me.schlaubi.kaesk.api.UseDeserializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CommandClassCompiler {

  private final Map<Class<?>, ArgumentDeserializer<?>> deserializers;

  public CommandClassCompiler(
      @NotNull final Map<Class<?>, ArgumentDeserializer<?>> deserializers) {
    this.deserializers = deserializers;
  }

  @NotNull
  public CompiledCommandClass compile(@NotNull final Object executor) {
    final Class<?> clazz = executor.getClass();
    Preconditions.checkArgument(clazz.isAnnotationPresent(CommandClass.class),
        "Class needs to be annotated with @CommandClass!");
    final CommandClass commandClass = clazz.getDeclaredAnnotation(CommandClass.class);
    // Check for invokes in class
    final List<String[]> invokes = findInvokes(clazz);
    // Plant command tree
    final CommandTreeElement tree = plantCommandTree(invokes, clazz, commandClass, executor);
    return new CompiledCommandClass(tree, commandClass.name(), commandClass.permission());
  }

  @NotNull
  private CommandTreeElement plantCommandTree(@NotNull final List<String[]> invokes,
      @NotNull final Class<?> clazz,
      @NotNull final CommandClass commandClass, @NotNull final Object executor) {
    // Convert methods into invokables
    final List<PotentialCommand> methods = readMethods(clazz);
    // Get root invokables
    final List<CommandInvokable> root = Collections.unmodifiableList(methods.stream()
        .filter(method -> method.getCommand().root()).map(method -> {
              Command annotation = method.getCommand();
              String permission = findPermission(annotation, commandClass);
              return new CommandInvokable(executor, method.getMethod(), "root",
                  compileParameters(annotation, method.getMethod()), permission,
                  annotation.consoleAllowed());
            }
        ).collect(Collectors.toList()));

    // Grow tree
    Preconditions.checkArgument(!root.isEmpty(), "You need to have at least one root command");
    return new CommandTreeElement(growCommandTree(invokes, executor, commandClass, methods, 1),
        root, 1);
  }

  @NotNull
  private Map<String, CommandTreeElement> growCommandTree(@NotNull final List<String[]> invokes,
      @NotNull final Object executor,
      @NotNull final CommandClass commandClass,
      @NotNull final List<PotentialCommand> methods, final int level) {
    return findInvokable(invokes, executor, commandClass, methods, level).entrySet().stream()
        .map(entry -> new SimpleEntry<>(entry.getKey(),
            new CommandTreeElement(
                growCommandTree(invokes, executor, commandClass, methods, level + 1),
                entry.getValue(), level)))
        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
  }

  @NotNull
  private Map<String, List<CommandInvokable>> findInvokable(@NotNull final List<String[]> invokes,
      @NotNull final Object executor,
      @NotNull final CommandClass commandClass,
      @NotNull final List<PotentialCommand> methods, final int level) {
    return invokes.stream().filter(invoke -> invoke.length == level)
        .map(invoke -> {
          List<CommandInvokable> invokables = Collections.unmodifiableList(methods.stream()
              .filter(invokable -> Arrays.equals(findInvoke(invokable.getMethod()), invoke))
              .map(invokable -> {
                Command command = invokable.getCommand();
                String permission = findPermission(command, commandClass);
                return new CommandInvokable(executor, invokable.getMethod(), command.name(),
                    compileParameters(command, invokable.getMethod()), permission,
                    command.consoleAllowed());
              })
              .collect(Collectors.toList()));
          return new SimpleEntry<>(invoke[invoke.length - 1],
              invokables);
        }).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
  }

  @NotNull
  private List<PotentialCommand> readMethods(@NotNull final Class<?> clazz) {
    return Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(Command.class)).map(method -> {
          Preconditions.checkArgument(Modifier.isPublic(method.getModifiers()),
              String.format("Command method must be public! Method: %s", method));
          CommandParents parents = findCommandParents(method);

          Command annotation = method.getAnnotation(Command.class);
          if (annotation.root()) {
            Preconditions.checkArgument(parents == null,
                String.format("Root command cannot have parents! Method: %s", method));
          }
          return new PotentialCommand(method, annotation, parents);
        }).collect(Collectors.toList()));
  }

  @Nullable
  private CommandParents findCommandParents(@NotNull final Method method) {
    CommandParents parents = null;
    if (method.isAnnotationPresent(CommandParents.class)) {
      parents = method.getAnnotation(CommandParents.class);
    }
    return parents;
  }

  @NotNull
  private List<String[]> findInvokes(@NotNull final Class<?> clazz) {
    return Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(Command.class) && !method
            .getAnnotation(Command.class).root())
        .map(this::findInvoke)
        .collect(Collectors.toList()));
  }

  @NotNull
  private String[] findInvoke(@NotNull final Method method) {
    List<String> parents = method.isAnnotationPresent(CommandParents.class) ?
        new ArrayList<>(Arrays.asList(method.getAnnotation(CommandParents.class).value()))
        : new ArrayList<>();
    final Command annotation = method.getAnnotation(Command.class);
    parents.add(annotation.name());
    return parents.toArray(new String[0]);
  }

  @NotNull
  private List<CommandParameter> compileParameters(@NotNull final Command invokable,
      @NotNull final Method method) {
    final Parameter first = method.getParameters()[0];
    Preconditions.checkArgument(!invokable.consoleAllowed() || first.getType().getName().equals(
        CommandSender.class.getName()),
        String.format("If console is allowed first argument has to be CommandSender. Method: %s",
            method));
    return Collections.unmodifiableList(Arrays.stream(method.getParameters())
        .skip(1) // sender argument
        .map(parameter -> {

          final Class<?> type = getParameterType(parameter);
          Preconditions.checkArgument(!type.isPrimitive(),
              String.format(
                  "Primitive types are not supported please use wrapper types. Method: %s",
                  method));
          Preconditions.checkArgument(deserializers.containsKey(type),
              String.format("Unknown parameter %s. Method: %s", type, method));
          final boolean isVarArg = parameter.isVarArgs();
          if (isVarArg) {
            Preconditions.checkState(deserializers.get(type).supportsVararg(),
                String.format("Type %s does not support var args! Method: %s", type, method));
          }
          final String name = findParameterName(parameter);
          final Class<? extends ArgumentDeserializer<?>> deserializer = findDeserializer(parameter);
          return new CommandParameter(type, isVarArg, name, deserializer);
        }).collect(Collectors.toList()));
  }

  @NotNull
  private String findParameterName(@NotNull final Parameter parameter) {
    if (parameter.isAnnotationPresent(CommandArgument.class)) {
      final CommandArgument annotation = parameter.getAnnotation(CommandArgument.class);
      return annotation.name();
    } else {
      return parameter.getName();
    }
  }

  @Nullable
  private Class<? extends ArgumentDeserializer<?>> findDeserializer(@NotNull final Parameter parameter) {
    if (parameter.isAnnotationPresent(UseDeserializer.class)) {
      final UseDeserializer annotation = parameter.getAnnotation(UseDeserializer.class);
      return annotation.value();
    } else {
      return null;
    }
  }

  @NotNull
  private Class<?> getParameterType(@NotNull final Parameter parameter) {
    final Class<?> type = parameter.getType();
    if (parameter.isVarArgs()) {
      return type.getComponentType();
    }
    return type;
  }

  @NotNull
  private String findPermission(@NotNull final Command command,
      @NotNull final CommandClass commandClass) {
    return
        command.permission().equals(Command.INHERIT_PERMISSION) ? commandClass.permission()
            : command.permission();
  }


  private static class PotentialCommand {

    @NotNull
    private final Method method;
    @NotNull
    private final Command command;
    @Nullable
    private final CommandParents parents;

    public PotentialCommand(@NotNull Method method,
        @NotNull Command command,
        @Nullable CommandParents parents) {
      this.method = method;
      this.command = command;
      this.parents = parents;
    }

    public @NotNull Method getMethod() {
      return method;
    }

    public @NotNull Command getCommand() {
      return command;
    }

    public @Nullable CommandParents getParents() {
      return parents;
    }
  }
}
