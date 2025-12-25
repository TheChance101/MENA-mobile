package net.thechance.mena.identity.presentation.screen.profile.imageCropper.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.toByteArray

fun cropImageToByteArray(
    painter: Painter,
    density: Density,
    layoutDirection: LayoutDirection,
    componentSize: IntSize,
    drawingSize: Size,
    translation: Offset,
    scale: Float
): ByteArray {
    val imageBitmap = ImageBitmap(componentSize.width, componentSize.height)
    drawOnBitmap(
        imageBitmap = imageBitmap,
        painter = painter,
        density = density,
        layoutDirection = layoutDirection,
        drawingSize = drawingSize,
        componentSize = componentSize,
        translation = translation,
        scale = scale
    )

    return imageBitmap.toByteArray(format = ImageFormat.PNG)
}

private fun drawOnBitmap(
    imageBitmap: ImageBitmap,
    painter: Painter,
    density: Density,
    layoutDirection: LayoutDirection,
    drawingSize: Size,
    componentSize: IntSize,
    translation: Offset,
    scale: Float
) {
    val canvas = Canvas(imageBitmap)
    val clipPath = createClipPath(componentSize)

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = layoutDirection,
        canvas = canvas,
        size = drawingSize
    ) {
        drawBlock(
            painter = painter,
            imageSize = drawingSize,
            clipPath = clipPath,
            translation = translation,
            scale = scale
        )
    }
}

private fun createClipPath(componentSize: IntSize): Path {
    val clipCircleRadius = componentSize.width / 2
    val clipCircleCenter = Offset(
        x = componentSize.width.toFloat() / 2,
        y = componentSize.height.toFloat() / 2
    )
    val clipBounds = Rect(
        left = clipCircleCenter.x - clipCircleRadius,
        top = clipCircleCenter.y - clipCircleRadius,
        right = clipCircleCenter.x + clipCircleRadius,
        bottom = clipCircleCenter.y + clipCircleRadius
    )
    return Path().apply { addOval(oval = clipBounds) }
}

private fun DrawScope.drawBlock(
    painter: Painter,
    imageSize: Size,
    clipPath: Path,
    translation: Offset,
    scale: Float
) {
    clipPath(path = clipPath) {
        with(painter) {
            translate(left = translation.x, top = translation.y) {
                scale(scale = scale, block = { draw(imageSize) })
            }
        }
    }
}