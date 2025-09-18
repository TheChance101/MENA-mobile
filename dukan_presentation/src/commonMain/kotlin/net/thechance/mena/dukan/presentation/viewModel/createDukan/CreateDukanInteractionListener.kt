package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.domain.entity.Dukan

interface CreateDukanInteractionListener {
    fun onButtonClicked()
    fun onBackClicked()
    fun onColorClicked(color: Long)
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
}