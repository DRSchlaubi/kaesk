package me.schlaubi.kaesk.internal;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandTreeElement {

  private final Map<String, CommandTreeElement> children;
  private final List<CommandInvokable> invokables;
  private final int level;

  public CommandTreeElement(
      @NotNull final Map<String, CommandTreeElement> children,
      @NotNull final List<CommandInvokable> invokables, int level) {
    this.children = children;
    this.invokables = invokables;
    this.level = level;
  }

  @NotNull
  public List<CommandInvokable> getInvokables() {
    return invokables;
  }

  @Nullable
  public CommandTreeElement findChild(final String name) {
    return children.get(name);
  }

  @NotNull
  public Map<String, CommandTreeElement> getChildren() {
    return children;
  }

  public int getLevel() {
    return level;
  }
}
