package net.thechance.mena.wallet.presentation.screen.statement_details

import net.thechance.mena.wallet.presentation.base.UiState

data class StatementDetailsScreenState(
    val statement: UiState<ByteArray> = UiState.Loading,
)