@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface StatementsHistoryEffect {
    data object NavigateBack : StatementsHistoryEffect
    data class NavigateToStatementDetails(val id: Uuid) : StatementsHistoryEffect
}