package net.thechance.mena.designsystem.presentation.component.drawer

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun ScaffoldScope.NavigationDrawer(
    onDismiss: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    dismissOnClickOutside: Boolean = true,
    drawerWidth: Dp = 586.dp,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(0.55f),
    drawerShape: Shape = RoundedCornerShape(
        topStart = Theme.radius.xl,
        bottomStart = Theme.radius.xl
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    val animatedOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else drawerWidth,
        animationSpec = tween(300),
        label = "drawer_offset"
    )

    if (isVisible || animatedOffset < drawerWidth) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val scrimAlpha = if (isVisible) 1f else 0f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor.copy(alpha = scrimAlpha * 0.55f))
                    .clickable(
                        enabled = dismissOnClickOutside && isVisible,
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = modifier
                        .width(drawerWidth)
                        .fillMaxHeight()
                        .offset { IntOffset(animatedOffset.value.toInt(), 0) }
                        .clip(drawerShape)
                        .background(backgroundColor)
                        .clickable(enabled = false) { }
                ) {
                    content()
                }
            }
        }
    }
}