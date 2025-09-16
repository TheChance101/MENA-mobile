package net.thechance.mena.dukan.presentation.viewModel.createDukan
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan.Style

fun Style.toUiState(): StyleUiState {
    return when (this) {
       Style.WIDE_IMAGE -> StyleUiState(
            orientation = DukanStyle.List,
            hasImage = true,
            label = "Wide image with list products"
        )
        Style.SMALL_IMAGE -> StyleUiState(
            orientation = DukanStyle.Grid,
            hasImage = true,
            label = "Small image with grid products"
        )
        Style.NO_IMAGE -> StyleUiState(
            orientation = DukanStyle.List,
            hasImage = false,
            label = "No dukan image"
        )
    }
}

fun List<Category>.toUiState(): List<DukanCategoryUiState> {
    return map { category ->
        DukanCategoryUiState(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}