package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun StatementHistoryBody(
    state: StatementsHistoryScreenState,
    listener: StatementsHistoryInteractionListener
) {
    when {
        state.statements.isEmpty() -> EmptyStatementsHistory(modifier = Modifier.fillMaxSize())
        else -> StatementsListContent(
            modifier = Modifier.fillMaxSize().padding(top = Theme.spacing._8),
            listener = listener,
            state = state
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun StatementHistoryBodyEmptyPreview() {
    val emptyState = StatementsHistoryScreenState(
        statements = emptyList()
    )

    val mockListener = object : StatementsHistoryInteractionListener {
        override fun onBackClicked() {}
        override fun onRetryLoadStatementsHistoryClicked() {}
        override fun onNextPageRequested() {}
        override fun onEditClicked() {}
        override fun onCancelEditModeClicked() {}

        override fun onStatementCardClicked(
            statement: StatementsHistoryScreenState.StatementItem,
            onViewStatementAvailable: (Boolean) -> Unit
        ) {
            onViewStatementAvailable(true)
        }

        override fun onDeleteClicked(
            statement: StatementsHistoryScreenState.StatementItem,
            onDeleteComplete: (Boolean) -> Unit
        ) {
            onDeleteComplete(true)
        }
    }

    MenaTheme {
        StatementHistoryBody(
            state = emptyState,
            listener = mockListener
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun StatementHistoryBodyWithDataPreview() {
    val mockStatements = List(10) { index ->
        StatementsHistoryScreenState.StatementItem(
            id = Uuid.parse("123e4567-e89b-12d3-a456-42661417400$index"),
            startDate = "01 Oct 2025",
            endDate = "15 Oct 2025",
            totalInflow = 1500.0 + index * 100,
            totalOutflow = 750.0 + index * 50,
            fileName = "Statement_Oct_1_15_$index.pdf"
        )
    }

    val state = StatementsHistoryScreenState(
        statements = mockStatements,
        isEditMode = false
    )

    val mockListener = object : StatementsHistoryInteractionListener {
        override fun onBackClicked() {}
        override fun onRetryLoadStatementsHistoryClicked() {}
        override fun onNextPageRequested() {}
        override fun onEditClicked() {}
        override fun onCancelEditModeClicked() {}

        override fun onStatementCardClicked(
            statement: StatementsHistoryScreenState.StatementItem,
            onViewStatementAvailable: (Boolean) -> Unit
        ) {
            onViewStatementAvailable(true)
        }

        override fun onDeleteClicked(
            statement: StatementsHistoryScreenState.StatementItem,
            onDeleteComplete: (Boolean) -> Unit
        ) {
            onDeleteComplete(true)
        }
    }

    MenaTheme {
        StatementHistoryBody(
            state = state,
            listener = mockListener
        )
    }
}