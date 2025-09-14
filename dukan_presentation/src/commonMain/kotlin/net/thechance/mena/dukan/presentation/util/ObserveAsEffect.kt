package net.thechance.mena.dukan.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component.DukanStyle

@Composable
fun <T> ObserveAsEffect(
    effects: Flow<T>,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                effects.collect(onEvent)
            }
        }
    }
}

data class StyleItem(
    val style: Dukan.Style,
    val name: String,
    val orientation: DukanStyle,
    val hasImage: Boolean
)

val styles = listOf(
    StyleItem(Dukan.Style.WIDE_IMAGE, "Wide Image", DukanStyle.HORIZONTAL, true),
    StyleItem(Dukan.Style.SMALL_IMAGE, "Small Image", DukanStyle.VERTICAL, true),
    StyleItem(Dukan.Style.NO_IMAGE, "No Image", DukanStyle.HORIZONTAL, false)
)