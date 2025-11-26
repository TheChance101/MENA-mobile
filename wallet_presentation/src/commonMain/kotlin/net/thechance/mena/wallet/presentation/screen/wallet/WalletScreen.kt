@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.download
import mena.wallet_presentation.generated.resources.downloaded_statements
import mena.wallet_presentation.generated.resources.ic_clock
import mena.wallet_presentation.generated.resources.my_wallet
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.StatementsHistoryScreenRoute
import net.thechance.mena.wallet.presentation.navigation.TransactionsHistoryScreenRoute
import net.thechance.mena.wallet.presentation.screen.wallet.component.BalanceCard
import net.thechance.mena.wallet.presentation.screen.wallet.component.LabeledButtonWithCircularIcon
import net.thechance.mena.wallet.presentation.screen.wallet.component.RefreshIcon
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun WalletMainScreen(
    navigateBack: () -> Unit,
    viewModel: WalletViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onWalletEffect(
                effect = effect,
                onNavigateBackClicked = navigateBack,
                navController = navController
            )
        }
    )

    WalletContent(state = state, interactionListener = viewModel)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun WalletContent(
    state: WalletScreenState,
    interactionListener: WalletInteractionListener
) {
    WalletScaffold(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface),
        topBar = {
            AppBar(
                title = stringResource(Res.string.my_wallet),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = { BackIcon() },
                trailingContent = { RefreshIcon(interactionListener) },
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
                .verticalScroll(rememberScrollState())
        ) {
            BalanceCard(
                state = state.balanceState,
                onRetry = interactionListener::onRetryLoadBalanceClicked,
                modifier = Modifier.padding(top = 16.dp)
            )

            LabeledButtonWithCircularIcon(
                icon = painterResource(Res.drawable.ic_clock),
                contentDescription = stringResource(Res.string.transactions_history),
                label = stringResource(Res.string.transactions_history),
                onClick = interactionListener::onTransactionHistoryClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )

            LabeledButtonWithCircularIcon(
                icon = painterResource(Res.drawable.download),
                contentDescription = stringResource(Res.string.downloaded_statements),
                label = stringResource(Res.string.downloaded_statements),
                onClick = interactionListener::onStatementHistoryClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

private fun onWalletEffect(
    effect: WalletEffect,
    onNavigateBackClicked: () -> Unit,
    navController: NavController
) {
    when (effect) {
        WalletEffect.NavigateBack -> onNavigateBackClicked()
        WalletEffect.NavigateToTransactionHistory -> {
            navController.navigate(TransactionsHistoryScreenRoute)
        }

        WalletEffect.NavigateToStatementHistory -> {
            navController.navigate(StatementsHistoryScreenRoute)
        }
    }
}

@Preview
@Composable
private fun WalletScreenPreview() {
    MenaTheme {
        WalletContent(
            state = WalletScreenState(
                balanceState = WalletScreenState.BalanceUiState(balance = 530320.55)
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