package com.aaron.compose.base

/**
 * @author aaronzzxup@gmail.com
 * @since 2024/12/1
 */

abstract class Defaults

abstract class DefaultsTarget<T : Defaults>(defaults: T) {

    val instance: T get() = _instance
    private var _instance: T = defaults

    protected val defaultsName = instance::class.java.name

    open fun set(instance: T) {
        _instance = instance
    }
}