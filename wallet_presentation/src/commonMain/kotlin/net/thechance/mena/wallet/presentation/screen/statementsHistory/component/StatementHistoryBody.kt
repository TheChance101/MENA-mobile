package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
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
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            listener = listener,
            state = state
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun StatementHistoryBodyPreview() {
    MenaTheme {
        StatementHistoryBody(
            state = StatementsHistoryScreenState(
                statements = List(10) { index ->
                    StatementsHistoryScreenState.StatementItem(
                        id = Uuid.parse("123e4567-e89b-12d3-a456-42661417400$index"),
                        startDate = "01 Oct 2025",
                        endDate = "15 Oct 2025",
                        totalInflow = "1000.0" ,
                        totalOutflow = "500.0",
                        fileName = "Statement_$index.pdf",
                        isDeleting = false
                    )
                },
                isEditMode = false,
                isLoading = false
            ),
            listener = object : StatementsHistoryInteractionListener {
                override fun onBackClicked() {}
                override fun onRetryLoadStatementsHistoryClicked() {}
                override fun onNextPageRequested() {}
                override fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem) {}
                override fun onEditClicked() {}
                override fun onCancelEditModeClicked() {}
                override fun onDeleteClicked(statement: StatementsHistoryScreenState.StatementItem) {}
            }
        )
    }
}