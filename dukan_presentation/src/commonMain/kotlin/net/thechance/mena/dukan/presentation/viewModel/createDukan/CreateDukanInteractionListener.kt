package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import org.maplibre.compose.camera.CameraPosition

interface CreateDukanInteractionListener {
    fun onNextOrCreateClicked()
    fun onBackClicked()
    fun onColorClicked(color: CreateDukanUiState.ColorUiState)
    fun onStyleClicked(style: CreateDukanUiState.Style)
    fun onClickUploadImage(image: ImageFile)
    fun onNameChanged(name: String)

    fun isCategorySelected(): (CreateDukanUiState.DukanCategoryUiState) -> Boolean
    fun onCategoryClicked(category: CreateDukanUiState.DukanCategoryUiState): Boolean
    fun onCategoryEnabled(category: CreateDukanUiState.DukanCategoryUiState): Boolean

    fun onNextClicked()
    fun onImageCrop(image: ImageBitmap)
    fun onCancelCrop()
    fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset,
    )

    fun onAddressChanged(address: String)
    fun onCameraMoved(camera: CameraPosition)
    fun onEditMapLocationClicked()
    fun onExpandLocationPicker()
    fun onConfirmLocationPicked()
    fun onCancelLocationPicker()
    fun onDismissSnackBar()
}