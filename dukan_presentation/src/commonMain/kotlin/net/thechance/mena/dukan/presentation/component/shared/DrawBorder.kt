package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

fun DrawScope.drawBorder(
    borderColor: Color,
    cornerRadiusValue: Float
) {
    val dashPattern = floatArrayOf(15f, 5f)
    val phase = 0f
    val strokeWidth = 1.dp
    val strokeWidthPx = strokeWidth.toPx()

    drawRoundRect(
        color = borderColor,
        topLeft = Offset(
            x = strokeWidthPx / 2,
            y = strokeWidthPx / 2
        ),
        size = Size(
            width = size.width - strokeWidthPx,
            height = size.height - strokeWidthPx
        ),
        cornerRadius = CornerRadius(
            x = cornerRadiusValue,
            y = cornerRadiusValue
        ),
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(intervals = dashPattern, phase),
        )
    )
}
