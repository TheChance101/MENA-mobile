package net.thechance.mena.wallet.presentation.screen.payment_result

sealed interface PaymentResultEffect {
    data object NavigateBack : PaymentResultEffect
}