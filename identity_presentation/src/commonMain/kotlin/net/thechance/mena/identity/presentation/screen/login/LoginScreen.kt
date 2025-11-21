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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.forget_password
import mena.identity_presentation.generated.resources.login
import mena.identity_presentation.generated.resources.login_prompt
import mena.identity_presentation.generated.resources.password
import mena.identity_presentation.generated.resources.register_now
import mena.identity_presentation.generated.resources.welcome_back
import mena.identity_presentation.generated.resources.you_are_new
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.components.LabeledInputPhoneNumber
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryScreen
import net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry.ResetPasswordPhoneEntryScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class LoginScreen : BaseScreen<
        LoginScreenViewModel,
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
        val focusManager = LocalFocusManager.current
        LaunchedEffect(state.showCountryBottomSheet) {
            if (state.showCountryBottomSheet) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }

        }

        Scaffold(
            overlays = {
                bottomSheet(state.showCountryBottomSheet) { showBottomSheet ->
                    CountryPicker(
                        isVisible = showBottomSheet,
                        currentCountry = state.currentCountry,
                        onClickConfirm = listener::onConfirmCountryItem,
                        onDismiss = listener::onDismissBottomSheet,
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
                        countryCode = state.currentCountry.callingCode,
                        countryFlag = painterResource(state.currentCountry.flagImage),
                        onClickCountry = listener::onPhoneCodeClicked
                    )

                    LabeledInputPassword(
                        password = state.password,
                        isPasswordVisible = state.isPasswordVisible,
                        onTogglePasswordVisibility = listener::onPasswordVisibilityToggled,
                        onChangePassword = listener::onPasswordChanged,
                        label = stringResource(Res.string.password),
                        modifier = Modifier.padding(top = Theme.spacing._16)
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
                            .padding(bottom = Theme.spacing._12)
                    )
                    AuthPrompt(
                        modifier = Modifier.imePadding(),
                        message = stringResource(Res.string.you_are_new),
                        actionLabel = stringResource(Res.string.register_now),
                        onActionClick = listener::onRegisterClicked
                    )
                }
            }
            ErrorSnackBar(
                errorMessage = state.errorMessage?.let { stringResource(it) },
                onDismiss = listener::clearErrorMessage,
                modifier = Modifier.systemBarsPadding()
            )
        }
    }

    override fun onEffect(
        effect: LoginScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is LoginScreenUIEffect.NavigateToRegister -> navigator.push(RegisterPhoneEntryScreen())
            LoginScreenUIEffect.NavigateToForgotPassword -> navigator.push(ResetPasswordPhoneEntryScreen())
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
            .padding(top = Theme.spacing._4),
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