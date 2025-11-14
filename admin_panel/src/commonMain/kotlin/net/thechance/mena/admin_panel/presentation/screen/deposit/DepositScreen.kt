package net.thechance.mena.admin_panel.presentation.screen.deposit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.screen.deposit.component.AmountInputField
import net.thechance.mena.admin_panel.presentation.screen.deposit.component.PhoneNumberInputField
import net.thechance.mena.admin_panel.presentation.screen.login.LoginEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.fill_the_wallet
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DepositScreen(viewModel: DepositViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    DepositScreenContent(state = state, interactionListener = viewModel)
}

@Composable
private fun DepositScreenContent(
    state: DepositScreenState,
    interactionListener: DepositInteractionListener
) {

    Column(modifier = Modifier.padding(top = 40.dp)) {
        PhoneNumberInputField(
            phoneNumber = state.phoneNumber,
            onPhoneChange = interactionListener::onPhoneNumberChanged,
            countryCode = state.country.callingCode,
            countryFlag =state.country.flagEmoji,
            onClickCountry = interactionListener::onCountryCodeSelected
        )
        AmountInputField(
            amount = state.amount,
            onAmountChanged =interactionListener::onAmountChanged
        )
        PrimaryButton(
            modifier = Modifier
                .width(70.dp)
                .align(Alignment.End)
                .padding(bottom = 16.dp),
            text = stringResource(Res.string.fill_the_wallet),
            onClick = interactionListener::onFillTheWalletButtonClicked,
            isEnabled = state.isFillWalletButtonEnabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
        )
    }
}