package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.toSize
import kotlin.math.abs

@Stable
class ImageCropperUiState(
    val minScale: Float,
    val maxScale: Float,
    initialState: ImageCropperUiModel
) {
    var state by mutableStateOf(initialState)

    val isZoomInEnabled by derivedStateOf {
        state.scale < maxScale
    }
    val isZoomOutEnabled by derivedStateOf {
        state.scale > minScale
    }

    fun zoomBy(gestureZoom: Float, pan: Offset) {
        val scale = (state.scale * gestureZoom).coerceIn(minScale, maxScale)
        updateState(state.copy(scale = scale, translation = state.translation + pan))
    }

    fun zoomIn(value: Float) {
        val scale = (state.scale + value).coerceAtMost(maxScale)
        updateState(newState = state.copy(scale = scale))
    }

    fun zoomOut(value: Float) {
        val scale = (state.scale - value).coerceAtLeast(minScale)
        updateState(newState = state.copy(scale = scale))
    }

    fun reset() {
        updateState(newState = state.copy(scale = minScale, translation = Offset.Zero))
    }

    fun resetAndUpdateImageSize(imageSize: IntSize) {
        reset()
        state = state.copy(imageSize = imageSize)
    }

    fun updateComponentSize(componentSize: IntSize) {
        state = state.copy(componentSize = componentSize)
    }

    fun cropToBitmap(
        painter: Painter,
        density: Density,
        layoutDirection: LayoutDirection,
    ): ImageBitmap {
        val imageBitmap = ImageBitmap(state.componentSize.width, state.componentSize.height)
        val canvas = Canvas(imageBitmap)
        val imageFloatSize = state.imageSize.toSize()
        val clipPath = createClipPath()

        CanvasDrawScope().draw(
            density = density,
            layoutDirection = layoutDirection,
            canvas = canvas,
            size = imageFloatSize
        ) {
            drawOnBitMapCanvas(
                painter = painter,
                imageSize = state.imageSize.toSize(),
                clipPath = clipPath
            )
        }

        return imageBitmap
    }

    private fun updateState(newState: ImageCropperUiModel) {
        val translation = newState.translation
        val translationXRange = calculateTranslationXRange(newState)
        val translationYRange = calculateTranslationYRange(newState)
        val maxOffsetOfX = translation.x.coerceIn(translationXRange)
        val maxOffsetOfY = translation.y.coerceIn(translationYRange)

        state = state.copy(
            scale = newState.scale,
            translation = translation.copy(maxOffsetOfX, maxOffsetOfY),
            imageSize = newState.imageSize,
            componentSize = newState.componentSize
        )
    }

    private fun createClipPath(): Path {
        val clipCircleRadius = state.componentSize.width / 2
        val clipCircleCenter = Offset(
            x = state.componentSize.width.toFloat() / 2,
            y = state.componentSize.height.toFloat() / 2
        )
        val clipBounds = Rect(
            left = clipCircleCenter.x - clipCircleRadius,
            top = clipCircleCenter.y - clipCircleRadius,
            right = clipCircleCenter.x + clipCircleRadius,
            bottom = clipCircleCenter.y + clipCircleRadius
        )
        return Path().apply { addOval(oval = clipBounds) }
    }

    private fun DrawScope.drawOnBitMapCanvas(
        painter: Painter,
        imageSize: Size,
        clipPath: Path
    ) {
        clipPath(path = clipPath) {
            with(painter) {
                translate(left = state.translation.x, top = state.translation.y) {
                    scale(scale = state.scale, block = { draw(imageSize) })
                }
            }
        }
    }

    private fun calculateTranslationXRange(newState: ImageCropperUiModel): ClosedFloatingPointRange<Float> {
        val (scale, _, imageSize, componentSize) = newState
        val initialHorizontalBounds = abs(imageSize.width - componentSize.width)
        val newAddedHorizontalBounds = abs(imageSize.width * (scale - 1f)) / 2
        val minValue = -newAddedHorizontalBounds - initialHorizontalBounds
        return minValue..newAddedHorizontalBounds
    }

    private fun calculateTranslationYRange(newState: ImageCropperUiModel): ClosedFloatingPointRange<Float> {
        val (scale, _, imageSize, componentSize) = newState
        val initialVerticalBounds = abs(imageSize.height - componentSize.height)
        val newAddedVerticalBounds = abs(imageSize.height * (scale - 1f)) / 2
        val minValue = -newAddedVerticalBounds - initialVerticalBounds
        return minValue..newAddedVerticalBounds
    }

    @Stable
    data class ImageCropperUiModel(
        val scale: Float = 1f,
        val translation: Offset = Offset.Zero,
        val imageSize: IntSize,
        val componentSize: IntSize
    )
}