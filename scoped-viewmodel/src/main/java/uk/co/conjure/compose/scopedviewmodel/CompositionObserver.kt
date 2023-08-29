package uk.co.conjure.compose.scopedviewmodel

import androidx.compose.runtime.RememberObserver

/**
 * This class is responsible for notifying the [StoreOwnerRegistry] when a composable is detached so
 * that the viewmodel can be cleared.
 */
class CompositionObserver(
    private val vmStore: StoreOwnerRegistry,
    private val scopeId: String
) : RememberObserver {

    override fun onRemembered() {
        // Nothing to do
    }

    override fun onForgotten() {
        vmStore.composableDetached(scopeId)
    }

    override fun onAbandoned() {
        vmStore.composableDetached(scopeId)
    }
}