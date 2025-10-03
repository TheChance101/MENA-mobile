package net.thechance.mena.identity.presentation.screen.forgetPassword

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.`continue`
import mena.identity_presentation.generated.resources.forget_password_prompt
import mena.identity_presentation.generated.resources.forget_password_prompt_title
import mena.identity_presentation.generated.resources.reset_password
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabeledInputPhoneNumber
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.forgetPasswordOtp.OtpScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ForgetPasswordScreen : BaseScreen<
        ForgetPasswordScreenViewModel,
        ForgetPasswordScreenUIState,
        ForgetPasswordScreenUIEffect,
        ForgetPasswordScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: ForgetPasswordScreenUIState,
        listener: ForgetPasswordScreenInteractionListener
    ) {
        Scaffold(
            overlays = {
                bottomSheet(isVisible = state.showCountryBottomSheet) {
                    CountryPicker(
                        isEnabled = state.countryPickerUIState.isEnabled,
                        countries = state.countryPickerUIState.countries,
                        onSelectCountryItem = listener::onSelectCountryItem,
                        onDismiss = listener::onDismissBottomSheet,
                        onClickConfirm = listener::onClickConfirmButton
                    )
                }
            },
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.reset_password),
                    onBackClicked = listener::onClickBack
                )
            }
        ) {
            AuthScreenContainer() {
                PageDescription(
                    title = stringResource(Res.string.forget_password_prompt_title),
                    subtitle = stringResource(Res.string.forget_password_prompt),
                )

                LabeledInputPhoneNumber(
                    phoneNumber = state.phoneNumber,
                    onPhoneChange = listener::onChangePhone,
                    countryCode = state.countryPickerUIState.currentCountry.callingCode,
                    countryFlag = painterResource(state.countryPickerUIState.currentCountry.flagImage),
                    onClickCountry = listener::onClickCountry
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(Res.string.`continue`),
                    onClick = listener::onClickContinue,
                    isEnabled = state.isContinueEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, top = 24.dp)
                )
            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage,
            onDismiss = listener::onClearErrorMessage,
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: ForgetPasswordScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            ForgetPasswordScreenUIEffect.NavigateBack -> navigator.pop()
            is ForgetPasswordScreenUIEffect.NavigateToOTP -> navigator.push(
                item = OtpScreen(
                    phoneNumber = effect.phoneNumber,
                    countryCode = effect.countryCode,
                    callingCode = effect.callingCode
                )
            )
        }
    }
}

