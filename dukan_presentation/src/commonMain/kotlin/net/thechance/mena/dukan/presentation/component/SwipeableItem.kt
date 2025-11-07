package net.thechance.mena.dukan.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import net.thechance.mena.dukan.presentation.util.modifiers.swipeableItem
import kotlin.math.roundToInt

@Composable
fun SwipeableItem(
    actionButton: @Composable (BoxScope.() -> Unit),
    modifier: Modifier = Modifier,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var actionButtonWidth by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged {
                    actionButtonWidth = it.width.toFloat()
                },
            contentAlignment = Alignment.Center,
            content = actionButton
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(-offset.value.roundToInt(), 0) }
                .swipeableItem(
                    actionButtonWidth = actionButtonWidth,
                    isRtl = isRtl,
                    onExpanded = onExpanded,
                    onCollapsed = onCollapsed,
                    offset = offset,
                    coroutineScope = coroutineScope
                )
        ) {
            content()
        }
    }
}