package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

sealed interface DukanLocationEffect {
    data object NavigateBack : DukanLocationEffect
}