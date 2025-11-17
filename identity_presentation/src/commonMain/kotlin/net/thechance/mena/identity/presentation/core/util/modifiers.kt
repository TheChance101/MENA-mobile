package net.thechance.mena.identity.presentation.core.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    color: Color,
    shape: RoundedCornerShape
): Modifier = this.drawWithContent {
    drawContent()
    val cornerRadiusPx = shape.topStart.toPx(size, this)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)

    drawRoundRect(
        color = color,
        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
        cornerRadius = CornerRadius(cornerRadiusPx)
    )
}