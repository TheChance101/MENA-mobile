package net.thechance.mena.identity.presentation.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.forget_password
import mena.identity_presentation.generated.resources.login
import mena.identity_presentation.generated.resources.login_prompt
import mena.identity_presentation.generated.resources.password
import mena.identity_presentation.generated.resources.register_now
import mena.identity_presentation.generated.resources.register_prompt
import mena.identity_presentation.generated.resources.welcome_back
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabelInputPassword
import net.thechance.mena.identity.presentation.components.LabeledInputPhoneNumber
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.forgetPassword.ForgetPasswordScreen
import net.thechance.mena.identity.presentation.screen.register.RegisterScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class LoginScreen(
) : BaseScreen<
        LoginScreenModel,
        LoginScreenUIState,
        LoginScreenUIEffect,
        LoginScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: LoginScreenUIState,
        listener: LoginScreenInteractionListener,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(state.showCountryBottomSheet) {
            if (state.showCountryBottomSheet)
                keyboardController?.hide()
        }

        Scaffold(
            overlays = {
                bottomSheet(state.showCountryBottomSheet) {
                    CountryPicker(
                        isEnabled = state.countryPickerUIState.isEnabled,
                        countries = state.countryPickerUIState.countries,
                        onSelectCountryItem = listener::onSelectCountryItem,
                        onDismiss = listener::onDismissBottomSheet,
                        onClickConfirm = listener::onClickConfirmButton
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
                        title = stringResource(Res.string.welcome_back),
                        subtitle = stringResource(Res.string.login_prompt),
                    )

                    LabeledInputPhoneNumber(
                        phoneNumber = state.phoneNumber,
                        onPhoneChange = listener::onPhoneChanged,
                        countryCode = state.countryPickerUIState.currentCountry.callingCode,
                        countryFlag = painterResource(state.countryPickerUIState.currentCountry.flagImage),
                        onClickCountry = listener::onPhoneCodeClicked
                    )

                    LabelInputPassword(
                        password = state.password,
                        isPasswordVisible = state.isPasswordVisible,
                        onTogglePasswordVisibility = listener::onPasswordVisibilityToggled,
                        onChangePassword = listener::onPasswordChanged,
                        label = stringResource(Res.string.password),
                    )

                    ForgetPasswordText(
                        onClick = listener::onForgotPasswordClicked
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.login),
                        onClick = listener::onLoginClicked,
                        isEnabled = state.isLoginEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    AuthPrompt(
                        modifier = Modifier.imePadding(),
                        message = stringResource(Res.string.register_prompt),
                        actionLabel = stringResource(Res.string.register_now),
                        onActionClick = listener::onRegisterClicked
                    )
                }
            }
            ErrorSnackBar(
                errorMessage = state.errorMessage,
                onDismiss = listener::clearErrorMessage
            )
        }
    }

    override fun onEffect(
        effect: LoginScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is LoginScreenUIEffect.NavigateToRegister -> navigator.push(RegisterScreen())
            LoginScreenUIEffect.NavigateToForgotPassword -> navigator.push(ForgetPasswordScreen())
            LoginScreenUIEffect.NavigateToHome -> {}
        }
    }
}

@Composable
fun ForgetPasswordText(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        TextButton(
            text = stringResource(Res.string.forget_password),
            isEnabled = true,
            onClick = onClick,
            contentColor = Theme.colorScheme.shadePrimary,
        )
    }
}