package uk.co.conjure.compose.scopedviewmodel

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID


val LocalViewModel: ProvidableCompositionLocal<Map<String, String>> = staticCompositionLocalOf {
    emptyMap()
}

/**
 * Creates a new scope for the given ViewModel type. Use [scopedViewModel] to retrieve the ViewModel
 * in any child composable.
 */
@Composable
inline fun <reified VM : ViewModel> CreateScope(
    factory: ViewModelProvider.Factory? = null,
    crossinline content: @Composable (VM) -> Unit
) {

    val activity = LocalContext.current.getActivity()
    val vmStore: StoreOwnerRegistry = viewModel(viewModelStoreOwner = activity)
    vmStore.checkAttached(activity)

    val scopeId = rememberSaveable { UUID.randomUUID().toString() }
    val owner = vmStore.getOwner(scopeId)


    val viewModel: VM = viewModel(VM::class.java, viewModelStoreOwner = owner, factory = factory)
    remember { CompositionObserver(vmStore, scopeId) }

    CompositionLocalProvider(
        LocalViewModel provides LocalViewModel.current.plus(viewModel::class.java.toString() to scopeId)
    ) {
        content(viewModel)
    }
}

/**
 * Retrieves a ViewModel from the current scope. The scope is determined by the nearest
 * enclosing [CreateScope].
 * @throws IllegalStateException if no scope is found
 */
@Composable
inline fun <reified VM : ViewModel> scopedViewModel(): VM {
    val map = LocalViewModel.current
    val scopeId = map[VM::class.java.toString()]
        ?: throw IllegalStateException("No scope found for ${VM::class.java}. Did you forget to call CreateScope?")

    val vmStore: StoreOwnerRegistry =
        viewModel(viewModelStoreOwner = LocalContext.current.getActivity())
    val owner = vmStore.getOwner(scopeId)
    return viewModel(viewModelStoreOwner = owner)
}

fun Context.getActivity(): ComponentActivity {
    return when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> this.baseContext.getActivity()
        else -> throw IllegalStateException("Context is not an activity")
    }
}