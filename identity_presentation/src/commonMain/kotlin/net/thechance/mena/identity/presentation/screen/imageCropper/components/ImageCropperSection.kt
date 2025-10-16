package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@Composable
fun ImageCropperSection(
    image: Painter,
    contentDescription: String,
    scale: Float,
    translation: Offset,
    modifier: Modifier = Modifier,
    onTransformation: (gestureZoom: Float, pan: Offset) -> Unit,
    updateImageSize: (IntSize) -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(modifier) {

        LaunchedEffect(Unit) {
            with(density) {
                updateImageSize(IntSize(maxWidth.roundToPx(), maxHeight.roundToPx()))
            }
        }

        Image(
            painter = image,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = translation.x,
                    translationY = translation.y
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        onTransformation(gestureZoom, pan)
                    }
                }
        )

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