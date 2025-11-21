@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.presentation.utils.formatAmount
import net.thechance.mena.wallet.presentation.utils.formatLocalDate
import kotlin.uuid.ExperimentalUuidApi

fun Statement.toUiState(): StatementsHistoryScreenState.StatementItem {
    return StatementsHistoryScreenState.StatementItem(
        id = id,
        startDate = formatLocalDate(date = startDate, outputFormat = "MMM dd yyyy"),
        endDate = formatLocalDate(date = endDate, outputFormat = "MMM dd yyyy"),
        totalInflow = formatAmount( totalInflows),
        totalOutflow = formatAmount(totalOutflows),
        fileName = fileName,
        isDeleting = false
    )
}