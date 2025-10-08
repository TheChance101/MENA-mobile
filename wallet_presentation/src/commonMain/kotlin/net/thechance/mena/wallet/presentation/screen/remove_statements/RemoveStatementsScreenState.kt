package net.thechance.mena.wallet.presentation.screen.remove_statements

import net.thechance.mena.wallet.presentation.model.SnackBarState

data class RemoveStatementsScreenState(
   val  statement:List<StatementUiState> = emptyList(),
    val snackBar: SnackBarState = SnackBarState()
)

data class StatementUiState(
    val id: String,
    val date: String,
    val amount: String,
    val isSelected: Boolean = false
)