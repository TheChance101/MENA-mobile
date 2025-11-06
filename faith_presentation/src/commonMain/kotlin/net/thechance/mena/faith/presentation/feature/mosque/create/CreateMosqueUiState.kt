package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal data class CreateMosqueUiState(
    val selectedImage: ImageSrc? = null,
    val croppedImage: ImageBitmap? = null,
    val address: String = "",
    val name: String = "",
    val location: Coordinate? = null,
    val isImageBeingCropped: Boolean = false,
    val isButtonEnabled: Boolean = false
)
