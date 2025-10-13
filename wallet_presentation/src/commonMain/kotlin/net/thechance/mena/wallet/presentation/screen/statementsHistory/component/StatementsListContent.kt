@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    val listState = rememberLazyListState()

    if (!isEditMode) {
        PaginationTrigger(
            list = state.statements,
            listState = listState,
            buffer = 2,
            loadNextItems = listener::onNextPageRequested
        )
    }

    val density = LocalDensity.current
    val maxRevealDistance = with(density) { 60.dp.toPx() }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(isEditMode) {
        offsetX.animateTo(
            targetValue = if (isEditMode) -maxRevealDistance else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16),
        contentPadding = PaddingValues(
            bottom = Theme.spacing._16
        ),
        state = listState
    ) {
        items(
            items = state.statements,
            key = { it.id.toString() }
        ) { statement ->
            AnimatedStatementItem(
                statement = statement,
                lastStatement = state.statements.last(),
                isEditMode = isEditMode,
                offsetX = offsetX.value.roundToInt(),
                onDelete = { listener.onDeleteClicked(id = statement.id) },
                onClick = { listener.onStatementCardClicked(id = statement.id) }
            )
        }

        if (state.isPaginationLoading && !isEditMode) {
            item {
                TransactionLoadingState(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing._16)
                )
            }
        }

        if (state.errorState != null && state.statements.isNotEmpty() && !isEditMode) {
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