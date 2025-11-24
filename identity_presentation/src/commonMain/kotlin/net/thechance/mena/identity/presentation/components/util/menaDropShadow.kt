package net.thechance.mena.identity.presentation.components.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


fun Modifier.menaDropShadow(
    shape: Shape,
    shadowColor: Color = Color(0x0F111D2E),
    radius: Dp = 8.dp,
    spread: Dp = 0.dp,
    alpha: Float = 0.6f,
    offset: DpOffset
) = this.then(
    Modifier.dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = radius,
            spread = spread,
            alpha = alpha,
            color = shadowColor,
            offset = offset
        )
    )
)
