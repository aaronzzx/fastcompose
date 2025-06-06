package com.aaron.composeaccessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * @author aaronzzxup@gmail.com
 * @since 2024/11/14
 */
abstract class ComponentAccessibilityService : AccessibilityService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Suppress("LeakingThis")
    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Suppress("LeakingThis")
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    override val lifecycle: Lifecycle = lifecycleDispatcher.lifecycle

    override val viewModelStore: ViewModelStore = ViewModelStore()

    @CallSuper
    override fun onCreate() {
        savedStateRegistryController.performRestore(null)
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @CallSuper
    override fun onServiceConnected() {
        lifecycleDispatcher.onServicePreSuperOnBind()
        super.onServiceConnected()
        onSetOverlay()
    }

    @CallSuper
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        viewModelStore.clear()
        super.onDestroy()
    }

    protected abstract fun onSetOverlay()
}