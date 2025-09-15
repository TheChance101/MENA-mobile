package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component.DukanStyle
fun Dukan.Style.toUiState(): StyleUiState {
    return when (this) {
        Dukan.Style.WIDE_IMAGE -> StyleUiState(
            style = this,
            orientation = DukanStyle.HORIZONTAL,
            hasImage = true,
            label = "Wide image with list products"
        )
        Dukan.Style.SMALL_IMAGE -> StyleUiState(
            style = this,
            orientation = DukanStyle.VERTICAL,
            hasImage = true,
            label = "Small image with grid products"
        )
        Dukan.Style.NO_IMAGE -> StyleUiState(
            style = this,
            orientation = DukanStyle.HORIZONTAL,
            hasImage = false,
            label = "No dukan image"
        )
    }
}
