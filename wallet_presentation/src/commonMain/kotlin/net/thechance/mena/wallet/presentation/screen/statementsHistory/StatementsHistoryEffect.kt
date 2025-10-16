@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import net.thechance.mena.wallet.presentation.utils.StorageLocation
import kotlin.uuid.ExperimentalUuidApi

sealed interface StatementsHistoryEffect {
    data object NavigateBack : StatementsHistoryEffect
    data class NavigateToStatementDetails(val statementLocation: StorageLocation) : StatementsHistoryEffect
}