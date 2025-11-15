@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi
import kotlin.uuid.ExperimentalUuidApi

fun MyDukanStatus.toUiState(): MainScreenUiState.DukanState {
    return MainScreenUiState.DukanState(
        name = this.dukanName,
        status = this.status.toUiState()
    )
}

fun Dukan.Status.toUiState(): DukanStatusUi {
    return when (this) {
        Dukan.Status.PENDING -> DukanStatusUi.Pending
        Dukan.Status.APPROVED -> DukanStatusUi.Approved
        Dukan.Status.REJECTED -> TODO()
    }
}

fun Dukan.toBestNearestUiState(): MainScreenUiState.BestNearestDukanUiState {
    return MainScreenUiState.BestNearestDukanUiState(
        id = id.toString(),
        name = name,
        imageUrl = imageUrl
    )
}

fun Dukan.toEditorPickUiState(): MainScreenUiState.EditorPickDukanUiState {
    return MainScreenUiState.EditorPickDukanUiState(
        id = id.toString(),
        name = name,
        imageUrl = imageUrl,
        isFavorite = isFavorite
    )
}

fun TopDiscountedDukanPreview.toUiState(): MainScreenUiState.DukanTopDiscount {
    return MainScreenUiState.DukanTopDiscount(
        id = id,
        imageUrl = imageUrl,
        discount = discount
    )
}
