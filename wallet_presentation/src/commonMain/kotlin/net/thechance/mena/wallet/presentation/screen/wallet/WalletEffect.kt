@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.wallet

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface WalletEffect {
    data object NavigateBack : WalletEffect
    data object NavigateToTransactionHistory : WalletEffect
    data object NavigateToStatementHistory : WalletEffect
    data class NavigateToPaymentScreen(
        val amount: Double,
        val receiverId: Uuid
    ): WalletEffect
}