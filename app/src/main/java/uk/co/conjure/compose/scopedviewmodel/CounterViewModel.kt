package uk.co.conjure.compose.scopedviewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    val value = mutableStateOf(0)
    fun increment() {
        value.value++
    }
}