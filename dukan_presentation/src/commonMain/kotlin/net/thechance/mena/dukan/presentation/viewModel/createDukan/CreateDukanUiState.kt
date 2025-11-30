package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import org.maplibre.compose.camera.CameraPosition

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: CreateDukanStep = CreateDukanStep.BASIC_INFORMATION,
    val isButtonEnabled: Boolean = false,
    val isNextCreateButtonLoading: Boolean = false,

    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
    val pointerLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(target = Position(29.0, 28.0), zoom = 1.0),
    val isMapLocked: Boolean = false,
    val address: String = "",
    val isLocationPickerExpanded: Boolean = false,

    val croppedImage: ImageBitmap? = null,
    val dukanCategories: List<DukanCategoryUiState> = emptyList(),
    val selectedCategories: Set<DukanCategoryUiState> = emptySet(),
    val isNameUnique: Boolean = true,
    val snackBarState: SnackBarUiState? = null,
    val isEditIconVisible: Boolean = false,
    val selectedImage: ImageSrc? = null,
    val isImageBeingCropped: Boolean = false,
    val dukanColors: List<ColorUiState> = emptyList(),
    val dukanStyles: List<DukanStyleUiState> = emptyList(),
    val selectedColor: ColorUiState? = null,
    val selectedStyle: Style? = null,
    val errorMessage: String? = null
) {

    data class CoordinatesUiState(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }

    data class DukanStyleUiState(
        val style: Style,
        val name: String,
    )

    data class ColorUiState(
        val id: String,
        val color: Long
    )

    data class DukanCategoryUiState(
        val id: String,
        val name: String,
        val imageUrl: String
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