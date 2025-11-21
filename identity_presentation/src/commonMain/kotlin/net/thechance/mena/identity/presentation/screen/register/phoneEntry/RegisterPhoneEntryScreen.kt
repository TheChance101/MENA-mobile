package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.login
import mena.identity_presentation.generated.resources.register
import mena.identity_presentation.generated.resources.register_prompt_description
import mena.identity_presentation.generated.resources.register_prompt_title
import mena.identity_presentation.generated.resources.you_already_have_account
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabeledInputPhoneNumber
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import net.thechance.mena.identity.presentation.screen.register.otp.RegisterOtpScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class RegisterPhoneEntryScreen : BaseScreen<
        RegisterPhoneEntryViewModel,
        RegisterPhoneEntryUIState,
        RegisterPhoneEntryUIEffect,
        RegisterPhoneEntryInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: RegisterPhoneEntryUIState,
        listener: RegisterPhoneEntryInteractionListener
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        LaunchedEffect(state.showCountryBottomSheet) {
            if (state.showCountryBottomSheet){
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
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.register_prompt_title),
                        subtitle = stringResource(Res.string.register_prompt_description),
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
                        text = stringResource(Res.string.register),
                        onClick = listener::onClickRegister,
                        isEnabled = state.isRegisterEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = Theme.spacing._24, bottom = Theme.spacing._12)
                    )

                    AuthPrompt(
                        modifier = Modifier.imePadding(),
                        message = stringResource(Res.string.you_already_have_account),
                        actionLabel = stringResource(Res.string.login),
                        onActionClick = listener::onClickLogin
                    )
                }
            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage?.let { stringResource(it) },
            onDismiss = listener::onClearErrorMessage,
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: RegisterPhoneEntryUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is RegisterPhoneEntryUIEffect.NavigateToOTP -> navigator.push(
                item = RegisterOtpScreen(effect.registerUIState)
            )

            RegisterPhoneEntryUIEffect.NavigateToLogin -> navigator.push(LoginScreen())
        }
    }
}

@Preview
@Composable
fun PreviewRegisterPhoneEntryScreen() {
    MenaTheme {
        RegisterPhoneEntryScreen().OnRender(
            state = RegisterPhoneEntryUIState(
                phoneNumber = "7901234567",
                currentCountry = MenaCountry.IRAQ,
                isRegisterEnabled = true
            ),
            listener = object : RegisterPhoneEntryInteractionListener {
                override fun onSelectCountryItem(country: MenaCountry) {}
                override fun onDismissBottomSheet() {}
                override fun onClickRegister() {}
                override fun onClickCountry() {}
                override fun onChangePhone(phone: String) {}
                override fun onClearErrorMessage() {}
                override fun onClickLogin() {}
            }
        )
    }
}