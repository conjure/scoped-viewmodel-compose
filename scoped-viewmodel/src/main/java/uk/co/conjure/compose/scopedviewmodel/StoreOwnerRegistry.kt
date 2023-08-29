package uk.co.conjure.compose.scopedviewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Registry for [ViewModelStoreOwner]s that are scoped to a particular composition.
 * This ViewModel is registered with the Activity's lifecycle and will clear the viewmodels.
 */
class StoreOwnerRegistry : ViewModel() {

    private var isActivityRegistered: Boolean = false
    private var isChangingConfigurations: Boolean = false
    private val map = mutableMapOf<String, ViewModelStoreOwner>()

    override fun onCleared() {
        map.values.forEach { it.viewModelStore.clear() }
        super.onCleared()
    }

    fun getOwner(key: String): ViewModelStoreOwner = (map[key] ?: ScopedViewModelStoreOwner().also {
        map[key] = it
    })

    fun composableDetached(key: String) {
        // TODO: This prevents the viewmodel from being cleared when the Composable is detached due
        //  to a configuration change. We need to make sure that the viewmodel is cleared when the
        //  Composition is recreated without the Composable. E.g. by observing the Composition
        if (isChangingConfigurations) return
        map.remove(key)?.also { owner -> owner.viewModelStore.clear() }
    }

    fun checkAttached(activity: ComponentActivity) {
        if (!isActivityRegistered) {
            isActivityRegistered = true
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    isChangingConfigurations = false
                }

                override fun onStop(owner: LifecycleOwner) {
                    if (activity.isChangingConfigurations) {
                        isChangingConfigurations = true
                    }
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    isActivityRegistered = false
                    owner.lifecycle.removeObserver(this)
                }
            })
        }
    }
}

/**
 * Simple ViewModelStoreOwner that can be used to create a new scope.
 */
class ScopedViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}