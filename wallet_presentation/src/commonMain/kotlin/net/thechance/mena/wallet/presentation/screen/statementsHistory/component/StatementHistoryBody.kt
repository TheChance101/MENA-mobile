package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator

@Composable
fun StatementHistoryBody(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        state.statements.isEmpty() -> EmptyStatementsHistory(modifier = Modifier.fillMaxSize())
        else -> StatementsListContent(
            modifier = Modifier.fillMaxSize().padding(top = Theme.spacing._8),
            listener = listener,
            state = state
        )
    }
}