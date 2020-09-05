package me.schlaubi.kaesk.internal;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

public class DelegatingBungeeCommand extends Command implements TabExecutor {

  private final BungeeCommandClientImpl commandClient;
  private final CompiledCommandClass compiledCommandClass;

  public DelegatingBungeeCommand(String name,
      BungeeCommandClientImpl commandClient,
      CompiledCommandClass compiledCommandClass) {
    super(name);
    this.commandClient = commandClient;
    this.compiledCommandClass = compiledCommandClass;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    commandClient.onCommand(compiledCommandClass, new BungeeCommandSender(sender), getName(), args);
  }

  @Override
  public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
    return commandClient.onTabComplete(compiledCommandClass, new BungeeCommandSender(sender), getName(), args);
  }


  private static class BungeeCommandSender implements me.schlaubi.kaesk.api.CommandSender<CommandSender> {

    private final CommandSender bungeeSender;

    private BungeeCommandSender(CommandSender bungeeSender) {
      this.bungeeSender = bungeeSender;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
      return bungeeSender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
      return bungeeSender.equals(ProxyServer.getInstance().getConsole());
    }

    @Override
    public void sendMessage(@NotNull String text) {
      bungeeSender.sendMessage(new TextComponent(text));
    }

    @Override
    public @NotNull CommandSender getActual() {
      return bungeeSender;
    }
  }
}
