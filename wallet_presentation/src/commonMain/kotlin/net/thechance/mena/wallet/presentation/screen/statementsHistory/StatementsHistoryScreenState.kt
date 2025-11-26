@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class StatementsHistoryScreenState(
    val statements: List<StatementItem> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val endOfPages: Boolean = false,
    val snackBar: SnackBarState = SnackBarState(),
    val isEditMode: Boolean = false,
) {
    data class StatementItem(
        val id: Uuid,
        val startDate: String,
        val endDate: String,
        val totalInflow: String,
        val totalOutflow: String,
        val fileName: String,
        val isDeleting: Boolean
    )
}