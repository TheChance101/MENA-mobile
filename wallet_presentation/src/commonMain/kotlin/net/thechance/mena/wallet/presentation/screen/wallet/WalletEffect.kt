@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.wallet

import kotlin.uuid.ExperimentalUuidApi

sealed interface WalletEffect {
    data object NavigateBack : WalletEffect
    data object NavigateToTransactionHistory : WalletEffect
    data object NavigateToStatementHistory : WalletEffect
}