package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.ColorUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import org.maplibre.compose.camera.CameraPosition

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onNextOrCreateClicked() {}
    override fun onBackClicked() {}
    override fun onColorClicked(color: ColorUiState) {}
    override fun onStyleClicked(style: CreateDukanUiState.Style) {}
    override fun onClickUploadImage(image: ImageFile) {}
    override fun onNameChanged(name: String) {}
    override fun onCategoryEnabled(category: DukanCategoryUiState): Boolean = true
    override fun isCategorySelected(): (DukanCategoryUiState) -> Boolean = { false }
    override fun onCategoryClicked(category: DukanCategoryUiState): Boolean {
        return true
    }
    override fun onNextClicked() {}
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