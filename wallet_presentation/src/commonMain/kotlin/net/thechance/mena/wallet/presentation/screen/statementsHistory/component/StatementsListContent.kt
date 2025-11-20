@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun StatementsListContent(
    listener: StatementsHistoryInteractionListener,
    state: StatementsHistoryScreenState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = state.statements,
        listState = listState,
        buffer = 2,
        loadNextItems = listener::onNextPageRequested
    )
    var previousEditMode by rememberSaveable{ mutableStateOf(false) }
    val density = LocalDensity.current
    val maxRevealDistance = with(density) { 60.dp.toPx() }
    val historyIconRevealDistance = with(density) { 20.dp.toPx() }
    val deleteButtonStartOffset = with(density) { 70.dp.toPx() }

    val cardOffsetX = remember { Animatable(0f) }
    val historyIconOffsetX = remember { Animatable(0f) }
    val deleteButtonOffsetX = remember { Animatable(deleteButtonStartOffset) }

    LaunchedEffect(state.isEditMode) {
        if (previousEditMode != state.isEditMode) {
            previousEditMode=state.isEditMode
            val animationSpec: AnimationSpec<Float> = tween(
                durationMillis = 400,
                easing = LinearEasing
            )

            launch {
                cardOffsetX.animateTo(
                    targetValue = if (state.isEditMode) -maxRevealDistance else 0f,
                    animationSpec = animationSpec
                )
            }

            launch {
                historyIconOffsetX.animateTo(
                    targetValue = if (state.isEditMode) -historyIconRevealDistance else 0f,
                    animationSpec = animationSpec
                )
            }

            launch {
                deleteButtonOffsetX.animateTo(
                    targetValue = if (state.isEditMode) 0f else deleteButtonStartOffset,
                    animationSpec = animationSpec
                )
            }
        }
        else{
            cardOffsetX.snapTo(if (state.isEditMode) -maxRevealDistance else 0f)
            historyIconOffsetX.snapTo(if (state.isEditMode) -historyIconRevealDistance else 0f)
            deleteButtonOffsetX.snapTo(if (state.isEditMode) 0f else deleteButtonStartOffset)
        }
    }

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        state = listState
    ) {
        items(
            items = state.statements,
            key = { it.id }
        ) { statement ->
            AnimatedStatementItem(
                statement = statement,
                isDividerVisible = statement != state.statements.lastOrNull(),
                isEditMode = state.isEditMode,
                cardOffsetX = cardOffsetX.value.roundToInt(),
                historyIconOffsetX = historyIconOffsetX.value.roundToInt(),
                deleteButtonOffsetX = deleteButtonOffsetX.value.roundToInt(),
                onDeleteClicked = { listener.onDeleteClicked(statement) },
                onStatementCardClicked = { listener.onStatementCardClicked(statement) }
            )
        }

        if (state.isPaginationLoading) {
            item {
                TransactionLoadingState(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }
        }

        if (state.errorState != null && state.statements.isNotEmpty()) {
            item {
                PrimaryButton(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .wrapContentSize(),
                    text = stringResource(Res.string.retry),
                    onClick = listener::onRetryLoadStatementsHistoryClicked,
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun StatementsListContentPreview() {
    val mockStatements = listOf(
        StatementsHistoryScreenState.StatementItem(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
            startDate = "01 Oct 2025",
            endDate = "15 Oct 2025",
            totalInflow = "1500.0",
            totalOutflow = "750.0",
            fileName = "Statement_Oct_1_15.pdf",
            isDeleting = false
        ),
        StatementsHistoryScreenState.StatementItem(
            id = Uuid.parse("223e4567-e89b-12d3-a456-426614174111"),
            startDate = "16 Oct 2025",
            endDate = "20 Oct 2025",
            totalInflow = "1200.0",
            totalOutflow = "500.0",
            fileName = "Statement_Oct_16_20.pdf",
            isDeleting = false
        )
    )

    val mockState = StatementsHistoryScreenState(
        statements = mockStatements,
        isEditMode = true,
        isPaginationLoading = false,
        errorState = null
    )

    val mockListener = object : StatementsHistoryInteractionListener {
        override fun onBackClicked() {}
        override fun onRetryLoadStatementsHistoryClicked() {}
        override fun onNextPageRequested() {}
        override fun onEditClicked() {}
        override fun onCancelEditModeClicked() {}
        override fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem) {}
        override fun onDeleteClicked(statement: StatementsHistoryScreenState.StatementItem) {}
    }

    MenaTheme {
        StatementsListContent(
            listener = mockListener,
            state = mockState,
            modifier = Modifier.padding(16.dp)
        )
    }
}