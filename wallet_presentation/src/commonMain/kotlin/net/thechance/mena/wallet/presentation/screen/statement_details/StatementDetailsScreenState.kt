package net.thechance.mena.wallet.presentation.screen.statement_details

import net.thechance.mena.wallet.presentation.base.ErrorState

data class StatementDetailsScreenState(
    val statement: ByteArray = ByteArray(0),
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null
)