package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal data class CreateMosqueUiState(
    val croppedImage: ImageBitmap? = null,
    val isImageBeingCropped: Boolean = false,
    val address: String = "",
    val name: String = "",
    val location: Coordinate? = null,
)