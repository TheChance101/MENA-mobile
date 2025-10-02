@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.FilterButton
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.FilterTransactionEmpty
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionFilterBottomSheet
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryCard
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryEmpty
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionDetails: (id: Uuid) -> Unit,
    navigateToExportTransaction: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionHistoryEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToTransactionDetails = navigateToTransactionDetails,
                navigateToExportTransaction = navigateToExportTransaction
            )
        }
    )

    TransactionHistoryContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
fun TransactionHistoryContent(
    state: TransactionHistoryScreenState,
    interactionListener: TransactionHistoryInteractionListener
) {
    WalletScaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.transactions_history),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
                trailingContent = {
                    Icon(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .clickable { interactionListener.onExportClicked() },
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = Res.string.share.toString()
                    )
                }
            )
        },
        overlays = {
            bottomSheet(state.isFilterVisible) {
                TransactionFilterBottomSheet(
                    uiState = state.filterState,
                    onDismiss = interactionListener::onDismissFilter,
                    onClickAddFilter = interactionListener::onApplyFilterClicked,
                    onResetClicked = interactionListener::onResetFilterClicked,
                    onTypeToggled = interactionListener::selectFilterType,
                    onStatusSelected = interactionListener::selectFilterStatus,
                    onFromClick = {
                        // TODO: Show date picker
                    },
                    onToClick = {
                        // TODO: Show date picker
                    }
                )
            }
        },
        snackBar = {
            SnackBarContainer(snackBarState = state.snackBar)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            item {
                if (state.history.isNotEmpty() || state.filterState.activeFilterCount != 0) {
                    FilterButton(
                        activeFilterCount = state.filterState.activeFilterCount,
                        hasActiveFilters = state.filterState.hasActiveFilters,
                        onClick = interactionListener::onFilterClicked
                    )
                }
            }
            when {
                state.history.isEmpty() && state.filterState.activeFilterCount == 0 -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            TransactionHistoryEmpty()
                        }
                    }
                }

                state.history.isEmpty() && state.filterState.activeFilterCount > 0 -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            FilterTransactionEmpty()
                        }
                    }
                }

                state.history.isNotEmpty() -> {
                    items(state.history) { transaction ->
                        TransactionHistoryCard(
                            transaction = transaction,
                            onTransactionCardClicked = {
                                interactionListener.onTransactionCardClicked(transaction.id)
                            }
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Theme.colorScheme.stroke)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun onTransactionHistoryEffect(
    effect: TransactionHistoryEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionDetails: (id: Uuid) -> Unit,
    navigateToExportTransaction: () -> Unit
) {
    when (effect) {
        TransactionHistoryEffect.NavigateBack -> onNavigateBackClicked()
        TransactionHistoryEffect.NavigateToExportTransaction -> navigateToExportTransaction()
        is TransactionHistoryEffect.NavigateToTransactionDetails -> {
            navigateToTransactionDetails(effect.id)
        }
    }
}