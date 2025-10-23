@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
sealed interface TransactionHistoryEffect {
    data object NavigateBack : TransactionHistoryEffect
    data object NavigateToExportTransaction : TransactionHistoryEffect
    data class NavigateToTransactionDetails(val id: Uuid) : TransactionHistoryEffect
}
