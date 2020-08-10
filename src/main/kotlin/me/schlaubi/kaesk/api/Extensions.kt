@file:Suppress("unused")

package me.schlaubi.kaesk.api

import org.bukkit.plugin.java.JavaPlugin

/**
 * Global function that does create a [CommandClient].
 *
 * @param builder lambda receiving a [CommandClientBuilder] that is applied before invoking [CommandClientBuilder.build]
 */
fun commandClient(builder: CommandClientBuilder.() -> Unit): CommandClient =
        CommandClientBuilder().apply(builder).build()

/**
 * Global function that does create a [CommandClient] and automatically sets the plugin instance.
 *
 * @see me.schlaubi.kaesk.api.commandClient
 */
fun JavaPlugin.commandClient(builder: CommandClientBuilder.() -> Unit): CommandClient =
        me.schlaubi.kaesk.api.commandClient {
            setPlugin(this@commandClient)
            builder(this)
        }

/**
 * Adds am [ArgumentDeserializer] for the type [T].
 *
 * @see CommandClientBuilder.addDeserializer
 */
inline fun <reified T
        > CommandClientBuilder.addDeserializer(deserializer: ArgumentDeserializer<T>): CommandClientBuilder = addDeserializer(T::class.java, deserializer)

/**
 * Convenience method that sets the plugin by its [PLUGIN_CLASS].
 */
inline fun <reified PLUGIN_CLASS : JavaPlugin> CommandClientBuilder.setPlugin(): CommandClientBuilder = setPlugin(PLUGIN_CLASS::class.java)

/**
 * Kotlin setter for [CommandClientBuilder.setPlugin].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var CommandClientBuilder.plugin: JavaPlugin
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setPlugin(value)
    }

/**
 * Kotlin setter for [CommandClientBuilder.setArgumentHandler].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var CommandClientBuilder.argumentHandler: InvalidArgumentHandler
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setArgumentHandler(value)
    }

/**
 * Kotlin setter for [CommandClientBuilder.setNoPermissionHandler].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var CommandClientBuilder.noPermissionHandler: NoPermissionHandler
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setNoPermissionHandler(value)
    }
