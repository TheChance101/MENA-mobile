@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import kotlin.uuid.ExperimentalUuidApi

interface StatementsHistoryInteractionListener {
    fun onBackClicked()
    fun onRetryLoadStatementsHistoryClicked()
    fun onNextPageRequested()
    fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem)
    fun onEditClicked()
}