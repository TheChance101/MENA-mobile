package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

@Stable
data class ImageCropperUiState(
    val scale: Float = 1f,
    val translation: Offset = Offset.Zero,
    val imageSize: IntSize = IntSize.Zero,
    val componentSize: IntSize = IntSize.Zero
)