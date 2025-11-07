package net.thechance.mena.dukan.presentation.util.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.swipeableItem(
    actionButtonWidth: Float,
    isRtl: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    offset: Animatable<Float, *>,
    coroutineScope: CoroutineScope
): Modifier = pointerInput(Unit) {
    detectHorizontalDragGestures(
        onHorizontalDrag = { _, dragAmount ->
            coroutineScope.launch {
                val adjustedDragAmount = if (isRtl) -dragAmount else dragAmount
                val newOffset = (offset.value - adjustedDragAmount).coerceIn(0f, actionButtonWidth)
                offset.snapTo(newOffset)
            }
        },
        onDragEnd = {
            coroutineScope.launch {
                if (offset.value >= actionButtonWidth / 2f) {
                    offset.animateTo(actionButtonWidth)
                    onExpanded()
                } else {
                    offset.animateTo(0f)
                    onCollapsed()
                }
            }
        }
    )
}
