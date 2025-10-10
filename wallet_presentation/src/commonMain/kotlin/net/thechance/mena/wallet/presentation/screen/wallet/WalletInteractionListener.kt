package net.thechance.mena.wallet.presentation.screen.wallet

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface WalletInteractionListener {
    fun onBackClicked()
    fun onRetryLoadBalanceClicked()
    fun onTransactionHistoryClicked()
    fun onStatementHistoryClicked()
    fun onPaymentClicked(amount: Double, receiverId: Uuid)
}