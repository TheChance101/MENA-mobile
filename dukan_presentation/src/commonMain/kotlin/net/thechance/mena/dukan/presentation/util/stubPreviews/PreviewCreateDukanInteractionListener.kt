package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.ColorUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import org.maplibre.compose.camera.CameraPosition

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onButtonClicked() {}
    override fun onBackClicked() {}
    override fun onColorClicked(color: ColorUiState) {}
    override fun onStyleClicked(style: CreateDukanUiState.Style) {}
    override fun onClickUploadImage(image: ImageSrc) {}
    override fun onNameChanged(name: String) {}
    override fun isCategorySelected(category: DukanCategoryUiState): Boolean {
        return false
    }

    override fun onCategoryToggled(category: DukanCategoryUiState) {}
    override fun isCategoryEnabled(category: DukanCategoryUiState): Boolean {
        return false
    }

    override fun onClickNext() {}
    override fun onImageCrop(image: ImageBitmap) {}
    override fun onCancelCrop() {}
    override fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {}
    override fun onAddressChanged(address: String){}
    override fun onCameraMoved(camera: CameraPosition) {}

    override fun onEditMapLocationClicked() {}
    override fun onDismissSnackBar() {}
}