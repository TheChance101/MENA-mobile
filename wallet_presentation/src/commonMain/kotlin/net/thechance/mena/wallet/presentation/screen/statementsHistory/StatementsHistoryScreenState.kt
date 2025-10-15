@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi

data class StatementsHistoryScreenState(
    val statements: List<StatementItem> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val endOfPages: Boolean = false,
    val snackBar: SnackBarState = SnackBarState(),
) {
    data class StatementItem(
        val id: Long,
        val startDate: String,
        val endDate: String,
        val totalInflow: Double,
        val totalOutflow: Double,
        val fileName: String
    )
}