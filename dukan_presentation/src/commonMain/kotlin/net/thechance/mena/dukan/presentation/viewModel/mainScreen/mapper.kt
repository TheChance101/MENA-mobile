package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.DukanPreview
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

fun MyDukanStatus.toUiState(): MainScreenUiState.DukanState{
    return MainScreenUiState.DukanState(
        name = this.dukanName,
        status = this.status.toUiState()
    )
}

fun Dukan.Status.toUiState():DukanStatusUi{
    return when(this){
        Dukan.Status.PENDING -> DukanStatusUi.Pending
        Dukan.Status.APPROVED -> DukanStatusUi.Approved
        Dukan.Status.REJECTED -> TODO()
    }
}

fun DukanPreview.toBestNearestUiState(): MainScreenUiState.BestNearestDukanUiState {
    return MainScreenUiState.BestNearestDukanUiState(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}

fun DukanPreview.toEditorPickUiState(): MainScreenUiState.EditorPickDukanUiState {
    return MainScreenUiState.EditorPickDukanUiState(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}