package net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.`continue`
import mena.identity_presentation.generated.resources.forget_password_prompt
import mena.identity_presentation.generated.resources.forget_password_prompt_title
import mena.identity_presentation.generated.resources.reset_password
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.LabeledInputPhoneNumber
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.screen.resetPassword.otp.ResetPasswordOtpScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ResetPasswordPhoneEntryScreen : BaseScreen<
    ResetPasswordPhoneEntryScreenViewModel,
    ResetPasswordPhoneEntryScreenUIState,
    ResetPasswordPhoneEntryScreenUIEffect,
    ResetPasswordPhoneEntryScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: ResetPasswordPhoneEntryScreenUIState,
        listener: ResetPasswordPhoneEntryScreenInteractionListener
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        LaunchedEffect(state.showCountryBottomSheet) {
            if (state.showCountryBottomSheet) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        }

        Scaffold(
            overlays = {
                bottomSheet(isVisible = state.showCountryBottomSheet) { showBottomSheet ->
                    CountryPicker(
                        isVisible = showBottomSheet,
                        currentCountry = state.currentCountry,
                        onDismiss = listener::onDismissBottomSheet,
                        onClickConfirm = listener::onSelectCountryItem,
                    )
                }
            },
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.reset_password),
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            AuthScreenContainer {
                PageDescription(
                    title = stringResource(Res.string.forget_password_prompt_title),
                    subtitle = stringResource(Res.string.forget_password_prompt),
                )

                LabeledInputPhoneNumber(
                    phoneNumber = state.phoneNumber,
                    onPhoneChange = listener::onChangePhone,
                    countryCode = state.currentCountry.callingCode,
                    countryFlag = painterResource(state.currentCountry.flagImage),
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
                        .padding(bottom = Theme.spacing._12, top = Theme.spacing._24)
                        .imePadding()
                )
            }
        }
    }

    override fun onEffect(
        effect: ResetPasswordPhoneEntryScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            ResetPasswordPhoneEntryScreenUIEffect.NavigateBack -> navigator.pop()
            is ResetPasswordPhoneEntryScreenUIEffect.NavigateToOTP -> navigator.push(
                item = ResetPasswordOtpScreen(
                    phoneNumber = effect.phoneNumber,
                    countryCode = effect.countryCode,
                    callingCode = effect.callingCode
                )
            )

            is ResetPasswordPhoneEntryScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}

