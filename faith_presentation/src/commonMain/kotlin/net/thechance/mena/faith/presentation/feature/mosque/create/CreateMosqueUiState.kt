package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState

internal data class CreateMosqueUiState(
    val croppedImage: ImageBitmap? = null,
    val address: String = "",
    val name: String = "",
    val location: MosqueUiState.Coordinate? = null,
    val mosqueLocation: MosqueUiState.Coordinate? = null,
    val offset: DpOffset? = null,
    val isButtonEnabled: Boolean = false,
    val successMessage: String? = null
)
