package net.thechance.mena.dukan.presentation.viewModel.checkout

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToChangeLocation : CheckoutEffect
    data class NavigateToConfirmPayment(val transactionId: String): CheckoutEffect
}