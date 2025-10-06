@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.presentation.utils.formatStatementDate
import kotlin.uuid.ExperimentalUuidApi

fun Statement.toUiState(): StatementsHistoryState.StatementItem {
    return StatementsHistoryState.StatementItem(
        id = id,
        startDate = formatStatementDate(startDate),
        endDate = formatStatementDate(endDate),
        totalInflow = totalInflows,
        totalOutflow = totalOutflows
    )
}