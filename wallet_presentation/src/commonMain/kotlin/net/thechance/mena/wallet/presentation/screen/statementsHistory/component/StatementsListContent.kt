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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi

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

    val density = LocalDensity.current
    val maxRevealDistance = with(density) { 60.dp.toPx() }
    val historyIconRevealDistance = with(density) { 20.dp.toPx() }
    val deleteButtonStartOffset = with(density) { 70.dp.toPx() }

    val cardOffsetX = remember { Animatable(0f) }
    val historyIconOffsetX = remember { Animatable(0f) }
    val deleteButtonOffsetX = remember { Animatable(deleteButtonStartOffset) }

    LaunchedEffect(state.isEditMode) {
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

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
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
                onDeleteClicked = { onDeleteComplete ->
                    listener.onDeleteClicked(
                        statement = statement,
                        onDeleteComplete = onDeleteComplete
                    )
                },
                onStatementCardClicked = { onViewStatementAvailable ->
                    listener.onStatementCardClicked(
                        statement,
                        onViewStatementAvailable = onViewStatementAvailable
                    )
                }
            )
        }

        if (state.isPaginationLoading) {
            item {
                TransactionLoadingState(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing._16)
                )
            }
        }

        if (state.errorState != null && state.statements.isNotEmpty()) {
            item {
                PrimaryButton(
                    modifier = Modifier
                        .padding(top = Theme.spacing._12)
                        .wrapContentSize(),
                    text = stringResource(Res.string.retry),
                    onClick = listener::onRetryLoadStatementsHistoryClicked,
                    contentPadding = PaddingValues(
                        vertical = Theme.spacing._8,
                        horizontal = Theme.spacing._16
                    )
                )
            }
        }
    }
}