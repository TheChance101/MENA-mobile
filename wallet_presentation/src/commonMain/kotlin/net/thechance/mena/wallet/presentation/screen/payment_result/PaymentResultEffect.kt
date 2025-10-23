@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface PaymentResultEffect {
    data object NavigateBack : PaymentResultEffect
    data class NavigateToTransactionDetails(val transactionId: Uuid) : PaymentResultEffect
    data object NavigateToPrePaymentScreen : PaymentResultEffect
}