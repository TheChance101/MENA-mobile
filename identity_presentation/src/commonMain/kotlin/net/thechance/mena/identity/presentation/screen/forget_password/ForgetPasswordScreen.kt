package net.thechance.mena.identity.presentation.screen.forget_password

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.forget_password_otp.OTPScreen
import net.thechance.mena.identity.presentation.screen.login.ErrorSnackBar
import net.thechance.mena.identity.presentation.screen.login.LabeledPhoneNumberInput
import org.jetbrains.compose.resources.painterResource

class ForgetPasswordScreen : BaseScreen<
        ForgetPasswordScreenModel,
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
                    title = "Reset password",
                    onBackClicked = listener::onBackClicked
                )
            }
        ) {
            AuthScreenContainer() {
                PageDescription(
                    title = "Reset your password",
                    subtitle = "please enter the phone number associated with your account, We'll send a OTP to help you regain access",
                )
                LabeledPhoneNumberInput(
                    phoneNumber = state.phoneNumber,
                    onPhoneChange = listener::onPhoneChanged,
                    countryCode = state.countryPickerUIState.currentCountry.callingCode,
                    countryFlag = painterResource(state.countryPickerUIState.currentCountry.flagImage),
                    onCountryClick = listener::onPhoneCodeClicked
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Continue",
                    onClick = listener::onContinueClicked,
                    isEnabled = state.isContinueEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

            }
            ErrorSnackBar(
                errorMessage = state.errorMessage,
                onDismiss = listener::clearErrorMessage
            )
        }
    }

    override fun onEffect(
        effect: ForgetPasswordScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            ForgetPasswordScreenUIEffect.NavigateBack -> navigator.pop()
            ForgetPasswordScreenUIEffect.NavigateToOTP -> navigator.push(OTPScreen())
        }
    }
}

@Composable
fun AuthAppBar(
    title: String,
    onBackClicked: () -> Unit
) {
    AppBar(
        title = title,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = "Back"
            )
        },
        onLeadingClick = onBackClicked
    )
}
