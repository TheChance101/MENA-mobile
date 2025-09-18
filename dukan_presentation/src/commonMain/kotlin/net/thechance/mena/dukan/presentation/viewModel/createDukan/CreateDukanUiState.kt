package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraPosition
import net.thechance.mena.dukan.domain.entity.Dukan

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: CreateDukanStep = CreateDukanStep.BASIC_INFORMATION,
    val isButtonEnabled: Boolean = false,
    val isButtonLoading: Boolean = false,

    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
    val pointerLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(target = Position(29.0, 28.0), zoom = 1.0),
    val isMapLocked: Boolean = false,
    val address: String = "",

    val croppedImage: ImageBitmap? = null,
    val dukanCategories: List<DukanCategoryUiState> = emptyList(),
    val selectedCategories: Set<DukanCategoryUiState> = emptySet(),
    val isNameUnique: Boolean = true,
    val showSnackBar: Boolean = false,
    val isEditIconVisible: Boolean = false,
    val selectedImage: ImageSrc? = null,
    val isImageBeingCropped: Boolean = false,
    val dukanColors: List<Long> = listOf(
        // TODO: Replace with colors fetched from backend
        0xFFE91E63,
        0xFF1146F3,
        0xFF4CAF50,
        0xFFE91E63,
        0xFF2196F3,
        0xFF4CAF80,
        0xFFE91E63,
        0xFF7196F9,
    ),
    val dukanStyles: List<DukanStyleUiState> = defaultDukanStyles,
    val selectedColor: Long? = null,
    val selectedStyle: Dukan.Style? = null,
    val errorMessage: String? = null
) {

    data class CoordinatesUiState(
        val latitude: Double = 28.0,
        val longitude: Double = 29.0,
    )

    enum class CreateDukanStep {
        BASIC_INFORMATION,
        SELECT_IMAGE,
        SELECT_LOCATION,
        SELECT_STYLE;

        companion object {
            val steps = entries
        }
    }
}

data class DukanStyleUiState(
    val style: Dukan.Style,
    val name: String,
)

data class DukanCategoryUiState(
    val id: String,
    val name: String,
    val imageUrl: String
)