package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberImageCropState(
    minScale: Float = 1f,
    maxScale: Float = 5f,
    imageSize: IntSize = LocalWindowInfo.current.containerSize,
    componentSize: IntSize = imageSize
) = rememberSaveable(saver = saver) {
    ImageCropperUiState(
        minScale, maxScale,
        ImageCropperUiState.ImageCropperUiModel(
            imageSize = imageSize,
            componentSize = componentSize
        )
    )
}

private val saver = Saver<ImageCropperUiState, Map<String, Any>>(
    save = { imageState ->
        mapOf(
            "scale" to imageState.state.scale,
            "translation" to imageState.state.translation,
            "imageSize" to imageState.state.imageSize,
            "componentSize" to imageState.state.componentSize,
            "minScale" to imageState.minScale,
            "maxScale" to imageState.maxScale
        )
    },
    restore = { map ->
        val uiModel = ImageCropperUiState.ImageCropperUiModel(
            imageSize = map["imageSize"] as IntSize,
            scale = map["scale"] as Float,
            translation = map["translation"] as Offset,
            componentSize = map["componentSize"] as IntSize
        )

        ImageCropperUiState(
            minScale = map["minScale"] as Float,
            maxScale = map["maxScale"] as Float,
            initialState = uiModel
        )
    }
)