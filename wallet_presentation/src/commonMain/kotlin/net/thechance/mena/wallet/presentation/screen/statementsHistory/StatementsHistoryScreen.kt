@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.edit
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_edit
import mena.wallet_presentation.generated.resources.retry
import mena.wallet_presentation.generated.resources.statements
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.ErrorView
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.EmptyStatementsHistory
import net.thechance.mena.wallet.presentation.screen.statementsHistory.component.StatementHistoryCard
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun StatementHistoryScreen(
    viewModel: StatementsHistoryViewModel = koinViewModel(),
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (id: Uuid) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onStatementHistoryEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToStatementDetails = navigateToStatementDetails
            )
        }
    )

    StatementHistoryContent(
        state = state, listener = viewModel
    )
}

@Composable
private fun StatementHistoryContent(
    state: StatementsHistoryState,
    listener: StatementsHistoryInteractionListener
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.statements),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = listener::onBackClicked,
                trailingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = stringResource(Res.string.edit),
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Theme.colorScheme.background.surfaceLow,
                                RoundedCornerShape(Theme.radius.md)
                            )
                            .clip(RoundedCornerShape(Theme.radius.md))
                            .clickable { listener.onEditClicked() }
                            .padding(10.dp)

                    )
                }
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = {
            listener.onRetryLoadStatementsHistoryClicked()
        }
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            state.errorState != null ->
                ErrorView(onRetry = { listener.onRetryLoadStatementsHistoryClicked() })

            state.statements.isEmpty() -> {
                EmptyStatementsHistory(modifier = Modifier.fillMaxSize())
            }

            else -> {
                StatementsListContent(
                    modifier = Modifier.fillMaxSize().padding(top = Theme.spacing._8),
                    listener = listener,
                    state = state,
                )
            }
        }
    }
}

@Composable
private fun StatementsListContent(
    listener: StatementsHistoryInteractionListener,
    state: StatementsHistoryState,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    PaginationTrigger(
        list = state.statements,
        listState = listState,
        buffer = 2,
        loadNextItems = listener::onNextPageRequested
    )

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        state = listState
    ) {
        if (state.statements.isNotEmpty()) {
            items(state.statements) { statement ->
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

            if (state.isPaginationLoading) {
                item {
                    TransactionLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Theme.spacing._16)
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
}

private fun onStatementHistoryEffect(
    effect: StatementsHistoryEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToStatementDetails: (id: Uuid) -> Unit
) {
    when (effect) {
        StatementsHistoryEffect.NavigateBack -> onNavigateBackClicked()
        is StatementsHistoryEffect.NavigateToStatementDetails -> navigateToStatementDetails(
            effect.id
        )
    }
}