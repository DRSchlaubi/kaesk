package me.schlaubi.kaesk.api.bungee

import me.schlaubi.kaesk.api.CommandClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Plugin

/**
 * Kotlin setter for [BungeeCommandClientBuilder.setPlugin].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var BungeeCommandClientBuilder.plugin: Plugin
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setPlugin(value)
    }

/**
 * Global function that does create a [CommandClient].
 *
 * @param builder lambda receiving a [BungeeCommandClientBuilder] that is applied before invoking [BungeeCommandClientBuilder.build]
 */
fun commandClient(builder: BungeeCommandClientBuilder.() -> Unit): CommandClient<CommandSender> =
        BungeeCommandClientBuilder().apply(builder).build()

/**
 * Global function that does create a [CommandClient] and automatically sets the plugin instance.
 *
 * @see me.schlaubi.kaesk.api.bungee.commandClient
 */
fun Plugin.commandClient(builder: BungeeCommandClientBuilder.() -> Unit): CommandClient<CommandSender> =
        me.schlaubi.kaesk.api.bungee.commandClient {
            setPlugin(this@commandClient)
            builder(this)
        }