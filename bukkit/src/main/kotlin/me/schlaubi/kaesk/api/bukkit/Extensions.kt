package me.schlaubi.kaesk.api.bukkit

import me.schlaubi.kaesk.api.CommandClient
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Convenience method that sets the plugin by its [PLUGIN_CLASS].
 */
inline fun <reified PLUGIN_CLASS : JavaPlugin> BukkitCommandClientBuilder.setPlugin(): BukkitCommandClientBuilder = setPlugin(PLUGIN_CLASS::class.java)

/**
 * Kotlin setter for [BukkitCommandClientBuilder.setPlugin].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var BukkitCommandClientBuilder.plugin: JavaPlugin
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setPlugin(value)
    }

/**
 * Global function that does create a [CommandClient].
 *
 * @param builder lambda receiving a [BukkitCommandClientBuilder] that is applied before invoking [BukkitCommandClientBuilder.build]
 */
fun commandClient(builder: BukkitCommandClientBuilder.() -> Unit): CommandClient<CommandSender> =
        BukkitCommandClientBuilder().apply(builder).build()

/**
 * Global function that does create a [CommandClient] and automatically sets the plugin instance.
 *
 * @see me.schlaubi.kaesk.api.bukkit.commandClient
 */
fun JavaPlugin.commandClient(builder: BukkitCommandClientBuilder.() -> Unit): CommandClient<CommandSender> =
        me.schlaubi.kaesk.api.bukkit.commandClient {
            setPlugin(this@commandClient)
            builder(this)
        }
