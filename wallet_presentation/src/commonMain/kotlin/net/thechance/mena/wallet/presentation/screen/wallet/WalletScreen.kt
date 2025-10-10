package net.thechance.mena.wallet.presentation.screen.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.download
import mena.wallet_presentation.generated.resources.downloaded_statements
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_clock
import mena.wallet_presentation.generated.resources.my_wallet
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.wallet.component.BalanceCard
import net.thechance.mena.wallet.presentation.screen.wallet.component.LabeledButtonWithCircularIcon
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WalletMainScreen(
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionHistory: () -> Unit,
    navigateToStatementsHistory: () -> Unit,
    viewModel: WalletViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onWalletEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToTransactionHistory = navigateToTransactionHistory,
                navigateToStatementsHistory = navigateToStatementsHistory
            )
        }
    )

    WalletContent(state = state, interactionListener = viewModel)
}

@Composable
private fun WalletContent(
    state: WalletScreenState,
    interactionListener: WalletInteractionListener
) {
    WalletScaffold(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface)
            .statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.my_wallet),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            BalanceCard(
                balance = state.balance,
                onRetry = interactionListener::onRetryLoadBalanceClicked,
                modifier = Modifier
                    .padding(top = 16.dp)
            )
            LabeledButtonWithCircularIcon(
                icon = painterResource(Res.drawable.ic_clock),
                contentDescription = stringResource(Res.string.transactions_history),
                label = stringResource(Res.string.transactions_history),
                onClick = interactionListener::onTransactionHistoryClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.spacing._24)
            )

            LabeledButtonWithCircularIcon(
                icon = painterResource(Res.drawable.download),
                contentDescription = stringResource(Res.string.downloaded_statements),
                label = stringResource(Res.string.downloaded_statements),
                onClick = interactionListener::onStatementHistoryClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.spacing._16)
            )
        }
    }
}

private fun onWalletEffect(
    effect: WalletEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionHistory: () -> Unit,
    navigateToStatementsHistory: () -> Unit
) {
    when (effect) {
        WalletEffect.NavigateBack -> onNavigateBackClicked()
        WalletEffect.NavigateToTransactionHistory -> navigateToTransactionHistory()
        WalletEffect.NavigateToStatementHistory -> navigateToStatementsHistory()
    }
}

@Preview
@Composable
private fun WalletScreenPreview() {
    MenaTheme {
        WalletContent(
            state = WalletScreenState(
                balance = UiState.Success(530320.55)
            ),
            interactionListener = object : WalletInteractionListener {
                override fun onBackClicked() {}
                override fun onRetryLoadBalanceClicked() {}
                override fun onTransactionHistoryClicked() {}
                override fun onStatementHistoryClicked() {}
            }
        )
    }
}