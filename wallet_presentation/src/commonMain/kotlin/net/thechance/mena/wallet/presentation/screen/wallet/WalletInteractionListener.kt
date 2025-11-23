package net.thechance.mena.wallet.presentation.screen.wallet

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface WalletInteractionListener {
    fun onBackClicked()
    fun onRetryLoadBalanceClicked()
    fun onTransactionHistoryClicked()
    fun onStatementHistoryClicked()
}