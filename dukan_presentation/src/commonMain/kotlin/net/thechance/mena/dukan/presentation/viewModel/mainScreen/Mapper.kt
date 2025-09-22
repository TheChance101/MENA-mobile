package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.domain.entity.Dukan
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
        Dukan.Status.APPROVED -> TODO()
        Dukan.Status.REJECTED -> TODO()
    }
}