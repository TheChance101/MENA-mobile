@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_trush
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        state = listState
    ) {
        items(
            items = state.statements,
            key = { it.id.toString() }
        ) { statement ->
            if (isEditMode) {
                AnimatedStatementItem(
                    statement = statement,
                    listener = listener
                )
            } else {
                StatementHistoryCard(
                    startDate = statement.startDate,
                    endDate = statement.endDate,
                    totalInflow = statement.totalInflow.toString(),
                    totalOutflow = statement.totalOutflow.toString(),
                    onStatementCardClicked = { listener.onStatementCardClicked(id = statement.id) }
                )

                if (state.statements.last() != statement) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Theme.colorScheme.stroke)
                    )
                }
            }
        }

        if (state.isPaginationLoading && !isEditMode) {
            item {
                TransactionLoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing._16)
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

@Composable
private fun AnimatedStatementItem(
    statement: StatementsHistoryScreenState.StatementItem,
    listener: StatementsHistoryInteractionListener,
) {
    var isDeleting by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isVisible,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            AnimatedStatementCard(
                isDeleting = isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 56.dp),
                statement = statement,
                listener = listener
            )

            AnimatedDeleteButton(
                isDeleting = isDeleting,
                onDeleteClick = {
                    isDeleting = true
                    scope.launch {
                        delay(300)
                        isVisible = false
                        listener.onDeleteClicked(id = statement.id)
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun AnimatedStatementCard(
    isDeleting: Boolean,
    statement: StatementsHistoryScreenState.StatementItem,
    modifier: Modifier = Modifier,
    listener: StatementsHistoryInteractionListener
) {
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutLinearInEasing
        )
    )

    StatementHistoryCard(
        startDate = statement.startDate,
        endDate = statement.endDate,
        totalInflow = statement.totalInflow.toString(),
        totalOutflow = statement.totalOutflow.toString(),
        onStatementCardClicked = { listener.onStatementCardClicked(id = statement.id) },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    )
}

@Composable
private fun AnimatedDeleteButton(
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidth = constraints.maxWidth.toFloat()

        val width by animateDpAsState(
            targetValue = if (isDeleting) maxWidth.dp else 48.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val height by animateDpAsState(
            targetValue = if (isDeleting) 0.dp else 48.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val cornerRadius by animateFloatAsState(
            targetValue = if (isDeleting) 20f else 100f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        val offsetY by animateDpAsState(
            targetValue = if (isDeleting) (-48).dp else 0.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )

        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .offset(y = offsetY)
                .clickable(
                    enabled = !isDeleting,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDeleteClick()
                },
            contentAlignment = Alignment.Center
        ) {
            val color = Theme.colorScheme.background.bgError
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius(cornerRadius.dp.toPx())
                )
            }
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ic_trush),
                contentDescription = null,
                tint = Theme.colorScheme.error
            )
        }
    }
}