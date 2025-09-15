package net.thechance.mena.dukan.presentation.screen.home

import net.thechance.mena.dukan.domain.entity.Dukan

fun Dukan.Status.toUiState(): MainScreenUiState.DukanStatusUi{
    return when(this){
        Dukan.Status.Pending -> MainScreenUiState.DukanStatusUi.Pending
        Dukan.Status.None -> MainScreenUiState.DukanStatusUi.None
    }
}