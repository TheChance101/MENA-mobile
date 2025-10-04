package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.presentation.base.UiState

data class ViewTransactionStatementScreenState(
    val statement: UiState<ByteArray> = UiState.Loading,
    val filterParams: TransactionFilterParams? = null
)