package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import org.maplibre.compose.camera.CameraPosition

interface CreateDukanInteractionListener {
    fun onButtonClicked()
    fun onBackClicked()
    fun onColorClicked(color: ColorUiState)
    fun onStyleClicked(style: Dukan.Style)
    fun onClickUploadImage()
    fun onClickUploadImage(image: ImageSrc)
    fun onNameChanged(name: String)
    fun isCategorySelected(): (DukanCategoryUiState) -> Boolean
    fun onCategorySelected(category: DukanCategoryUiState): Boolean
    fun onCategoryDeselected(category: DukanCategoryUiState): Boolean
    fun onCategoryEnabled(category: DukanCategoryUiState): Boolean
    fun onCLickNext()
    fun onImageCrop(image: ImageBitmap)
    fun onCancelCrop()
    fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset,
    )
    fun onAddressChanged(address: String)
    fun onCameraMoved(camera: CameraPosition)
    fun onEditMapLocationClicked()
}