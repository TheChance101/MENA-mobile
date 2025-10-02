package net.thechance.mena.dukan.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry

@Composable
fun <T> NavBackStackEntry.ObserveSavedStateEvent(
    key: String,
    onEvent: (T) -> Unit
) {
    val state = savedStateHandle
        .getStateFlow<T?>(key, null)
        .collectAsStateWithLifecycle()

    LaunchedEffect(state.value) {
        state.value?.let { value ->
            onEvent(value)
            savedStateHandle.remove<T>(key)
        }
    }
}