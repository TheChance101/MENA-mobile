package net.thechance.mena.faith.presentation.utils.imageCrop

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CropShape
import com.attafitamim.krop.core.crop.CropperStyle

@Stable
fun createCropperStyle(
    backgroundColor: Color = Color.Black,
    overlayColor: Color = Color.Black.copy(alpha = .5f),
    touchRadius: Dp = 20.dp,
    strokeWidth: Float = 2f,
    handleColor: Color = Color.Red,
    cornerSize: Float = 0.1f
): CropperStyle = object : CropperStyle {
    override val backgroundColor: Color = backgroundColor
    override val overlayColor: Color = overlayColor
    override val handles: List<Offset> = listOf(
        Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f), Offset(0f, 1f)
    )
    override val touchRad: Dp = touchRadius
    override val shapes: List<CropShape> = listOf(RectangleCropShape)
    override val aspects: List<AspectRatio> = emptyList()
    override val autoZoom: Boolean = false

    override fun DrawScope.drawCropRect(region: Rect) {
        if (region.isEmpty) return

        val drawRegion = region
        val smallerSide = drawRegion.width.coerceAtMost(drawRegion.height)
        val actualStrokeWidth = (smallerSide * 0.015f).coerceAtLeast(strokeWidth)
        val actualCornerSize = smallerSide * cornerSize
        val midLineLengthH = drawRegion.width * 0.2f
        val cap = StrokeCap.Square

        handles.forEach { (xRel, yRel) ->
            val x = drawRegion.left + xRel * drawRegion.width
            val y = drawRegion.top + yRel * drawRegion.height

            when {
                xRel == 0f && yRel == 0f -> {
                    drawLine(
                        handleColor,
                        Offset(x, y),
                        Offset(x + actualCornerSize, y),
                        actualStrokeWidth,
                        cap
                    )
                    drawLine(
                        handleColor,
                        Offset(x, y),
                        Offset(x, y + actualCornerSize),
                        actualStrokeWidth,
                        cap
                    )
                }

                xRel == 1f && yRel == 0f -> {
                    drawLine(
                        handleColor,
                        Offset(x - actualCornerSize, y),
                        Offset(x, y),
                        actualStrokeWidth,
                        cap
                    )
                    drawLine(
                        handleColor,
                        Offset(x, y),
                        Offset(x, y + actualCornerSize),
                        actualStrokeWidth,
                        cap
                    )
                }

                xRel == 1f && yRel == 1f -> {
                    drawLine(
                        handleColor,
                        Offset(x - actualCornerSize, y),
                        Offset(x, y),
                        actualStrokeWidth,
                        cap
                    )
                    drawLine(
                        handleColor,
                        Offset(x, y - actualCornerSize),
                        Offset(x, y),
                        actualStrokeWidth,
                        cap
                    )
                }

                xRel == 0f && yRel == 1f -> {
                    drawLine(
                        handleColor,
                        Offset(x, y - actualCornerSize),
                        Offset(x, y),
                        actualStrokeWidth,
                        cap
                    )
                    drawLine(
                        handleColor,
                        Offset(x, y),
                        Offset(x + actualCornerSize, y),
                        actualStrokeWidth,
                        cap
                    )
                }
            }
        }

        val midTop = Offset(
            drawRegion.left + drawRegion.width * 0.5f - midLineLengthH * 0.5f,
            drawRegion.top
        )
        val midBottom = Offset(
            drawRegion.left + drawRegion.width * 0.5f - midLineLengthH * 0.5f,
            drawRegion.bottom
        )

        drawLine(handleColor, midTop, midTop + Offset(midLineLengthH, 0f), actualStrokeWidth)
        drawLine(handleColor, midBottom, midBottom + Offset(midLineLengthH, 0f), actualStrokeWidth)
    }
}


@Stable
object RectangleCropShape : CropShape {
    override fun asPath(rect: Rect) = Path().apply {
        addRect(rect)
    }
}