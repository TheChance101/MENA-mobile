package net.thechance.mena.trends.presentation.shared.component.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 1.dp,
    phase: Float = 0f,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 6.dp,
    cornerRadius: Dp = 8.dp,
) = this.then(
    drawBehind {
        val path =
            Path().apply {
                addRoundRect(
                    RoundRect(
                        rect =
                            Rect(
                                left = 0f,
                                top = 0f,
                                right = size.width,
                                bottom = size.height,
                            ),
                        cornerRadius = CornerRadius(cornerRadius.toPx()),
                    ),
                )
            }

        val dashPath =
            PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                phase = phase,
            )

        drawPath(
            path = path,
            color = color,
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = dashPath
                )
        )
    }
)