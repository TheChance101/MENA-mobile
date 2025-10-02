@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionErrorState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryEmpty
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionsListContent
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.ScrollingDetecting
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
    val listState = rememberLazyListState()
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
        interactionListener = viewModel,
        listState = listState,
    )
}

@Composable
fun TransactionHistoryContent(
    state: TransactionHistoryScreenState,
    interactionListener: TransactionHistoryInteractionListener,
    listState: LazyListState,
) {
    ScrollingDetecting(
        state = state,
        listState = listState,
        onLoadMore = interactionListener::onNextPageRequested
    )
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
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { interactionListener.onExportClicked() },
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = Res.string.share.toString()
                    )
                },
            )
        }
    ) {
        when {
            state.isLoading && state.history.isEmpty() -> {
                TransactionLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Theme.colorScheme.background.surface),
                )
            }

            state.isError != null && state.history.isEmpty() -> {
                TransactionErrorState(
                    modifier = Modifier.fillMaxSize(),
                    onRetry = interactionListener::onRetry
                )
            }

            state.history.isEmpty() -> {
                TransactionHistoryEmpty(modifier = Modifier.fillMaxSize())
            }

            else -> {
                TransactionsListContent(
                    interactionListener = interactionListener,
                    state = state,
                    listState = listState
                )
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
        TransactionHistoryEffect.NavigateToFilterBottomSheet -> {/*TODO: navigate to filter bottom sheet*/
        }

        is TransactionHistoryEffect.NavigateToTransactionDetails -> {
            navigateToTransactionDetails(effect.id)
        }
    }
}





