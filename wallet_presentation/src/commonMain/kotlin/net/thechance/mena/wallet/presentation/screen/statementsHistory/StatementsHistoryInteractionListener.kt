@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface StatementsHistoryInteractionListener {
    fun onBackClicked()
    fun onRetryLoadStatementsHistoryClicked()
    fun onNextPageRequested()
    fun onStatementCardClicked(id: Uuid)
    fun onEditClicked()
}