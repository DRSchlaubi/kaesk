@file:Suppress("unused")

package me.schlaubi.kaesk.api

/**
 * Adds am [ArgumentDeserializer] for the type [T].
 *
 * @see AbstractCommandClientBuilder.addDeserializer
 */
inline fun <BUILDER : AbstractCommandClientBuilder<*, BUILDER>, reified T
        > AbstractCommandClientBuilder<*, BUILDER>.addDeserializer(deserializer: ArgumentDeserializer<T>): BUILDER = addDeserializer(T::class.java, deserializer)

/**
 * Kotlin setter for [AbstractCommandClientBuilder.setArgumentHandler].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var AbstractCommandClientBuilder<*, *>.argumentHandler: InvalidArgumentHandler
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setArgumentHandler(value)
    }

/**
 * Kotlin setter for [AbstractCommandClientBuilder.setNoPermissionHandler].
 *
 * @throws UnsupportedOperationException when trying to get the property
 */
var AbstractCommandClientBuilder<*, *>.noPermissionHandler: NoPermissionHandler
    get() = throw UnsupportedOperationException("These properties are only here for setters")
    set(value) {
        setNoPermissionHandler(value)
    }
