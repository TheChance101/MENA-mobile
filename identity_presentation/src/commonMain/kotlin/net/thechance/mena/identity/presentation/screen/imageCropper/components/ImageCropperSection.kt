package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.toIntSize

@Composable
fun ImageCropperSection(
    image: Painter,
    scale: Float,
    translation: Offset,
    modifier: Modifier = Modifier,
    onTransformation: (gestureZoom: Float, pan: Offset) -> Unit,
    updateComponentSize: (IntSize) -> Unit,
    updateImageSize: (IntSize) -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(modifier) {

        val widthScale = remember(image) {
            with(density) { maxWidth.roundToPx() / image.intrinsicSize.width }
        }

        val componentSize = remember {
            with(density) {
                IntSize(maxWidth.roundToPx(), maxHeight.roundToPx())
            }
        }

        val imageSize = remember(image) {
            with(density) {
                val size = image.intrinsicSize.toDpSize() * widthScale
                size.copy(
                    width = size.width,
                    height = max(size.height, componentSize.height.toDp())
                )
            }
        }

        LaunchedEffect(Unit) {
            updateComponentSize(componentSize)
        }

        LaunchedEffect(image) {
            with(density) { updateImageSize(imageSize.toSize().toIntSize()) }
        }

        Canvas(
            modifier = Modifier
                .size(imageSize)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        onTransformation(gestureZoom, pan)
                    }
                }
        ) {
            with(image) {
                translate(left = translation.x, top = translation.y) {
                    scale(
                        scale = scale,
                        pivot = Offset(imageSize.width.toPx() / 2, imageSize.height.toPx() / 2),
                        block = { draw(imageSize.toSize()) }
                    )
                }
            }
        }

        OverlayCanvas()
    }
}

@Composable
private fun OverlayCanvas() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
    ) {
        drawRect(
            color = Color.Black.copy(0.32f),
            size = size,
        )

        drawCircle(
            color = Color.Transparent,
            radius = size.width / 2,
            center = center,
            blendMode = BlendMode.Clear
        )
    }
}