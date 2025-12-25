package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.toByteArray

fun createQrCodeByteArray(
    painter: Painter,
    density: Density,
    layoutDirection: LayoutDirection,
    screenSize: IntSize
): ByteArray {
    val imageBitmap = createBitmap(
        painter = painter,
        screenSize = screenSize,
        density = density,
        layoutDirection = layoutDirection
    )
    return imageBitmap.toByteArray(format = ImageFormat.PNG)
}

private fun createBitmap(
    painter: Painter,
    density: Density,
    layoutDirection: LayoutDirection,
    screenSize: IntSize
): ImageBitmap {
    val imageBitmap = ImageBitmap(
        width = screenSize.width,
        height = screenSize.height
    )
    drawOnBitmap(
        imageBitmap = imageBitmap,
        painter = painter,
        density = density,
        layoutDirection = layoutDirection,
        drawingSize = screenSize.toSize()
    )

    return imageBitmap
}

private fun drawOnBitmap(
    imageBitmap: ImageBitmap,
    painter: Painter,
    density: Density,
    layoutDirection: LayoutDirection,
    drawingSize: Size
) {
    val canvas = Canvas(imageBitmap)
    val targetSize = with(density) { 240.dp.toPx() }
    val imageSize = Size(
        width = targetSize,
        height = targetSize
    )
    val imageTranslationOffset = Offset(
        x = drawingSize.width / 2 - imageSize.width / 2,
        y = drawingSize.height / 2 - imageSize.height / 2
    )

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = layoutDirection,
        canvas = canvas,
        size = drawingSize
    ) {
        drawRect(
            color = Color(0xFFF2F4F7),
            size = drawingSize
        )

        with(painter) {
            translate(top = imageTranslationOffset.y, left = imageTranslationOffset.x) {
                draw(
                    size = Size(width = targetSize, height = targetSize)
                )
            }
        }
    }
}