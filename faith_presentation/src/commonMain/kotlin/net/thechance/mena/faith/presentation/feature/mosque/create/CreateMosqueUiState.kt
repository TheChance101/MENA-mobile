package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal data class CreateMosqueUiState(
    val id: String = "",
    val croppedImage: ImageBitmap? = null,
    val isImageBeingCropped: Boolean = false,
    val address: String = "",
    val mosqueName: String = "",
    val centerOfMap: Coordinate? = null,
)