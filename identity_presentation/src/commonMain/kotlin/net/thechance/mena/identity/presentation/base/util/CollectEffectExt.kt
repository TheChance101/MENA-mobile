package net.thechance.mena.identity.presentation.base.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@Suppress("ComposableNaming")
@Composable
fun <E> Flow<E>.collectAsEffectWithLifeCycle(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<E>,
) {
    LaunchedEffect(this) {
        flowWithLifecycle(lifecycle, minActiveState)
            .collect(collector)
    }
}