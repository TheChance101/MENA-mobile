package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


class TopNotchShape(
    private val offset: Float,
    private val cutoutWidth: Dp,
    private val cutoutDepth: Dp,
    private val cornerRadius: Dp,
    private val cutoutRoundness: Float = DEFAULT_CUTOUT_ROUNDNESS_MULTIPLIER,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutWidthPx = density.run { cutoutWidth.toPx() }
        val cutoutDepthPx = density.run { cutoutDepth.toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }

        return Path().apply {
            val halfCutoutWidth = cutoutWidthPx / 2f + 12
            val cutoutLeftX = cutoutCenterX - halfCutoutWidth
            val cutoutRightX = cutoutCenterX + halfCutoutWidth

            moveTo(x = 0F, y = size.height)

            if (cutoutLeftX > 0) {
                drawTopLeftEdge(cutoutLeftX, cornerRadiusPx)
            }

            lineTo(cutoutLeftX, 0f)

            drawCutout(cutoutCenterX, cutoutDepthPx, cutoutRightX)

            if (cutoutRightX < size.width) {
                drawTopRightEdge(cutoutRightX, size, cornerRadiusPx)
            }

            lineTo(x = size.width, y = size.height)
            close()
        }
    }

    private fun Path.drawTopLeftEdge(
        cutoutLeftX: Float,
        cornerRadiusPx: Float
    ) {
        val cornerDiameter = if (cutoutLeftX >= cornerRadiusPx) {
            cornerRadiusPx * 2
        } else {
            cutoutLeftX * 2
        }
        arcTo(
            rect = Rect(
                left = 0f,
                top = 0f,
                right = cornerDiameter,
                bottom = cornerDiameter
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )
    }

    private fun Path.drawCutout(
        cutoutCenterX: Float,
        cutoutDepth: Float,
        cutoutRightX: Float
    ) {
        val cutoutLeftX = cutoutCenterX - (cutoutRightX - cutoutCenterX)
        val controlPointOffset = cutoutDepth * cutoutRoundness

        cubicTo(
            x1 = cutoutLeftX + controlPointOffset,
            y1 = 0f,
            x2 = cutoutCenterX - controlPointOffset,
            y2 = cutoutDepth,
            x3 = cutoutCenterX,
            y3 = cutoutDepth,
        )
        cubicTo(
            x1 = cutoutCenterX + controlPointOffset,
            y1 = cutoutDepth,
            x2 = cutoutRightX - controlPointOffset,
            y2 = 0f,
            x3 = cutoutRightX,
            y3 = 0f,
        )
    }

    private fun Path.drawTopRightEdge(
        cutoutRightX: Float,
        size: Size,
        cornerRadiusPx: Float,
    ) {
        val cornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) {
            cornerRadiusPx * 2
        } else {
            (size.width - cutoutRightX) * 2
        }
        arcTo(
            rect = Rect(
                left = size.width - cornerDiameter,
                top = 0f,
                right = size.width,
                bottom = cornerDiameter
            ),
            startAngleDegrees = -90.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false
        )
    }

    companion object {
        private const val DEFAULT_CUTOUT_ROUNDNESS_MULTIPLIER = 0.8f
    }
}

@Preview
@Composable
private fun ShapePreview() {
    val parentWidthPx = with(LocalDensity.current) { 328.dp.toPx() }
    Box(
        modifier = Modifier
            .size(328.dp, 128.dp)
            .background(
                color = Color.White, shape = TopNotchShape(
                    offset = parentWidthPx / 2,
                    cutoutDepth = 26.dp,
                    cutoutWidth = 100.dp,
                    cornerRadius = 24.dp,
                )
            )
    )
}