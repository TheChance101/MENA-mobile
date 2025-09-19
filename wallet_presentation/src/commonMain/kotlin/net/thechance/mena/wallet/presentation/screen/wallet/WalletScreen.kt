package net.thechance.mena.wallet.presentation.screen.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.my_wallet
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.wallet.component.BalanceCard
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WalletScreen(viewModel: WalletViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    observeUiEffects(viewModel)
    walletContent(state, viewModel)
}

@Composable
private fun observeUiEffects(viewModel: WalletViewModel) {
    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is WalletEffect.NavigateBack -> { /* TODO: Handle navigation back */ }
        }
    }
}

@Composable
private fun walletContent(
    state: WalletScreenState,
    listener: WalletInteractionListener,
    modifier: Modifier = Modifier
) {
    val isError = state.balance is UiState.Error

    WalletScaffold(
        modifier = modifier.statusBarsPadding(),
        topBar = { topBar(listener::onBackClicked) },
        snackBar = { SnackBarContainer(state.snackBar) },
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp),
        isErrorMode = isError,
        onRetryClicked = listener::onRetryClicked,
        content = { paddingValues ->
            mainContent(state, listener, paddingValues)
        }
    )
}

@Composable
private fun mainContent(
    state: WalletScreenState,
    listener: WalletInteractionListener,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(paddingValues)
    ) {
        BalanceCard(
            balance = state.balance,
            onRetry = listener::onRetryLoadBalanceClicked,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun topBar(onBack: () -> Unit) {
    AppBar(
        title = stringResource(Res.string.my_wallet),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_button)
            )
        },
        onLeadingClick = onBack
    )
}

@Composable
@Preview
private fun WalletScreenPreview() {
    MenaTheme {
        walletContent(
            state = WalletScreenState(balance = UiState.Success(530320.55)),
            listener = object : WalletInteractionListener {
                override fun onBackClicked() {}
                override fun onRetryLoadBalanceClicked() {}
                override fun onRetryClicked() {}
            }
        )
    }
}