package net.thechance.mena.dukan.presentation.viewModel.checkout

sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToChangeLocation : CheckoutEffect
}