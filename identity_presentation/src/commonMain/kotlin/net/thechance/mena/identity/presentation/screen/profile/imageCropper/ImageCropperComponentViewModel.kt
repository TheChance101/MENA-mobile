package net.thechance.mena.identity.presentation.screen.profile.imageCropper

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Stable
class ImageCropperComponentViewModel(
    private val minScale: Float,
    private val maxScale: Float,
    initialState: ImageCropperUiState
) : ViewModel(), ImageCropperComponentInteractionListener {
    var state by mutableStateOf(initialState)
    val isZoomInEnabled by derivedStateOf { state.scale < maxScale }
    val isZoomOutEnabled by derivedStateOf { state.scale > minScale }

    private val _effect = MutableSharedFlow<ImageCropperComponentEffect>()
    val effect = _effect.asSharedFlow().throttleFirst(500).mapNotNull { it }

    override fun zoomBy(gestureZoom: Float, pan: Offset) {
        val scale = (state.scale * gestureZoom).coerceIn(minScale, maxScale)
        updateState(state.copy(scale = scale, translation = state.translation + pan))
    }

    override fun zoomIn(value: Float) {
        val scale = (state.scale + value).coerceAtMost(maxScale)
        updateState(newState = state.copy(scale = scale))
    }

    override fun zoomOut(value: Float) {
        val scale = (state.scale - value).coerceAtLeast(minScale)
        updateState(newState = state.copy(scale = scale))
    }

    override fun reset() {
        updateState(newState = state.copy(scale = minScale, translation = Offset.Zero))
    }

    override fun resetAndUpdateImageSize(imageSize: IntSize) {
        reset()
        state = state.copy(imageSize = imageSize)
    }

    override fun updateComponentSize(componentSize: IntSize) {
        state = state.copy(componentSize = componentSize)
    }

    override fun saveImageToGallery(imageByteArray: ByteArray) {
        viewModelScope.launch {
            sendNewEffect(ImageCropperComponentEffect.SaveImage(imageByteArray))
        }
    }

    override fun onUploadAnotherImageClicked(imageByteArray: ByteArray) {
        viewModelScope.launch {
            sendNewEffect(ImageCropperComponentEffect.UploadAnotherImage(imageByteArray))
        }
    }

    suspend fun sendNewEffect(newEffect: ImageCropperComponentEffect) {
        _effect.emit(newEffect)
    }

    private fun updateState(newState: ImageCropperUiState) {
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

    private fun calculateTranslationXRange(newState: ImageCropperUiState): ClosedFloatingPointRange<Float> {
        val (scale, _, imageSize, componentSize) = newState
        val initialHorizontalBounds = abs(imageSize.width - componentSize.width)
        val newAddedHorizontalBounds = abs(imageSize.width * (scale - 1f)) / 2
        val minValue = -newAddedHorizontalBounds - initialHorizontalBounds
        return minValue..newAddedHorizontalBounds
    }

    private fun calculateTranslationYRange(newState: ImageCropperUiState): ClosedFloatingPointRange<Float> {
        val (scale, _, imageSize, componentSize) = newState
        val initialVerticalBounds = abs(imageSize.height - componentSize.height)
        val newAddedVerticalBounds = abs(imageSize.height * (scale - 1f)) / 2
        val minValue = -newAddedVerticalBounds - initialVerticalBounds
        return minValue..newAddedVerticalBounds
    }

    @OptIn(ExperimentalTime::class)
    private fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
        require(periodMillis > 0)
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }
}