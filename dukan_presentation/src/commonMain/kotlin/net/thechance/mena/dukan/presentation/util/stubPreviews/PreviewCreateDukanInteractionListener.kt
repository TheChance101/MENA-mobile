package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.StyleUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onButtonClicked() {}
    override fun onBackClicked() {}
    override fun onColorClicked(color: Long){}
    override fun onStyleClicked(style: StyleUiState) {}
    override fun onClickUploadImage() {}
    override fun onClickUploadImage(image: ImageSrc) {}
    override fun onNameChanged(name: String) {}
    override fun onCategorySelected(category: DukanCategoryUiState): Boolean = true
    override fun onCategoryDeselected(category: DukanCategoryUiState): Boolean = true
    override fun onCategoryEnabled(category: DukanCategoryUiState): Boolean = true
    override fun isCategorySelected(): (DukanCategoryUiState) -> Boolean = { false }
    override fun onCLickNext() {}
    override fun onImageCrop(image: ImageBitmap) {}
    override fun onCancelCrop() {}
}