package me.schlaubi.kaesk.api.bungee;

import me.schlaubi.kaesk.api.CommandClient;
import net.md_5.bungee.api.CommandSender;

/**
 * Bungeecord specific command client.
 *
 * @see BungeeCommandClientBuilder
 * @see CommandClient
 */
public interface BungeeCommandClient extends CommandClient<CommandSender> {

}
