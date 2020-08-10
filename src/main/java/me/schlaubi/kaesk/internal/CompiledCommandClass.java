package me.schlaubi.kaesk.internal;

import org.jetbrains.annotations.NotNull;

class CompiledCommandClass {
  @NotNull
  final CommandTreeElement commandTree;
  @NotNull
  final String name;
  @NotNull
  final String permission;

  CompiledCommandClass(
      @NotNull CommandTreeElement commandTree,
      @NotNull String name, @NotNull String permission) {
    this.commandTree = commandTree;
    this.name = name;
    this.permission = permission;
  }

  public @NotNull CommandTreeElement getCommandTree() {
    return commandTree;
  }

  public @NotNull String getName() {
    return name;
  }

  public @NotNull String getPermission() {
    return permission;
  }
}
