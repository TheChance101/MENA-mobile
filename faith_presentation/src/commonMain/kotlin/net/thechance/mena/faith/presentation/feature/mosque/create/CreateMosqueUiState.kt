package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import org.maplibre.compose.camera.CameraPosition

data class CreateMosqueUiState(
    val croppedImage: ImageBitmap? = null,
    val address: String = "",
    val name: String = "",
    val location: MosqueUiState.Coordinate? = null,
    val mosqueLocation: MosqueUiState.Coordinate? = null,
    val cameraPosition: CameraPosition = CameraPosition(
        target = Position(
            latitude = 29.203231755958047,
            longitude = 22.39869322710709
        ), zoom = 1.6
    ),
    val offset: DpOffset? = null,
    val isButtonEnabled: Boolean = false,
    val successMessage: String? = null
)
