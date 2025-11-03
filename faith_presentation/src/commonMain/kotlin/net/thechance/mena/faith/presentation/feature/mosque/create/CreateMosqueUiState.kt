package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal data class CreateMosqueUiState(
    val selectedImage: ImageSrc? = null,
    val croppedImage: ImageBitmap? = ImageBitmap(
        width = 100,
        height = 100,
        config = ImageBitmapConfig.Argb8888,
        hasAlpha = true
    ),
    val address: String = "",
    val name: String = "",
    val location: Coordinate? = Coordinate(26.820553, 30.802498),
    val isImageBeingCropped: Boolean = false,
    val isButtonEnabled: Boolean = false
)

