package net.thechance.mena.admin_panel.presentation.screen.deposit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.designSystem.theme.EmojiTheme
import net.thechance.mena.admin_panel.presentation.screen.deposit.component.AmountInputField
import net.thechance.mena.admin_panel.presentation.screen.deposit.component.PhoneNumberInputField
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.deposit
import net.thechance.mena.admin_panel.resources.fill_a_wallet
import net.thechance.mena.admin_panel.resources.fill_a_wallet_description
import net.thechance.mena.admin_panel.resources.fill_the_wallet
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DepositScreen(viewModel: DepositViewModel = koinViewModel()) {
    EmojiTheme {
        val state by viewModel.state.collectAsStateWithLifecycle()
        DepositScreenContent(state = state, interactionListener = viewModel)
    }
}

@Composable
private fun DepositScreenContent(
    state: DepositScreenState,
    interactionListener: DepositInteractionListener
) {
    PanelScaffold(
        topBar = { DepositTopBar() },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        isLoading = state.isCountriesLoading,
        errorState = state.errorState,
        onRetry = interactionListener::onRetryClicked
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 118.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(506.dp)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(Res.string.fill_a_wallet),
                    style = Theme.typography.title.medium,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = stringResource(Res.string.fill_a_wallet_description),
                    style = Theme.typography.body.medium,
                    color = Theme.colorScheme.shadeSecondary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                PhoneNumberInputField(
                    phoneNumber = state.phoneNumber,
                    onPhoneChange = interactionListener::onPhoneNumberChanged,
                    selectedCountry = state.selectedCountry,
                    availableCountries = state.availableCountries,
                    onCountrySelected = interactionListener::onCountryCodeChanged,
                    modifier = Modifier.fillMaxWidth()
                )

                AmountInputField(
                    modifier = Modifier.padding(top = 16.dp),
                    amount = state.amount,
                    onAmountChanged = interactionListener::onAmountChanged,
                )

                PrimaryButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 64.dp)
                        .width(123.dp),
                    text = stringResource(Res.string.fill_the_wallet),
                    onClick = interactionListener::onFillTheWalletButtonClicked,
                    isEnabled = state.isFillWalletButtonEnabled,
                    isLoading = state.isDepositProcessLoading,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
                )
            }
        }
    }
}

@Composable
private fun DepositTopBar() {
    AppBar(
        title = stringResource(Res.string.deposit),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}

