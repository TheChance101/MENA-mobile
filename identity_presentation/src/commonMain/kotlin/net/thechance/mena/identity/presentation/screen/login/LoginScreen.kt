package net.thechance.mena.identity.presentation.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.forget_password
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_close_eye
import mena.identity_presentation.generated.resources.ic_lock
import mena.identity_presentation.generated.resources.ic_open_eye
import mena.identity_presentation.generated.resources.login
import mena.identity_presentation.generated.resources.login_prompt
import mena.identity_presentation.generated.resources.password
import mena.identity_presentation.generated.resources.phone_number
import mena.identity_presentation.generated.resources.register_now
import mena.identity_presentation.generated.resources.register_prompt
import mena.identity_presentation.generated.resources.welcome_back
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.CountryPicker
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.PhoneNumberInput
import net.thechance.mena.identity.presentation.screen.forget_password.ForgetPasswordScreen
import net.thechance.mena.identity.presentation.screen.register.RegisterScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class LoginScreen : BaseScreen<
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
                    Text(
                        text = stringResource(Res.string.phone_number),
                        style = Theme.typography.title.small,
                        color = Theme.colorScheme.shadePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    PhoneNumberInput(
                        state.countryPickerUIState.currentCountry.callingCode,
                        painterResource(state.countryPickerUIState.currentCountry.flagImage),
                        onCountryClick = listener::onPhoneCodeClicked,
                        phoneNumber = state.phoneNumber,
                        onPhoneChange = listener::onPhoneChanged
                    )
                    Text(
                        text = stringResource(Res.string.password),
                        style = Theme.typography.title.small,
                        color = Theme.colorScheme.shadePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp)
                    )

                    TextField(
                        value = state.password,
                        onValueChanged = listener::onPasswordChanged,
                        hint = "",
                        trailingIcon = painterResource(
                            if (state.isPasswordVisible) Res.drawable.ic_open_eye
                            else Res.drawable.ic_close_eye
                        ),
                        leadingIcon = painterResource(Res.drawable.ic_lock),
                        showTrailingDivider = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        onTrailingIconClick = listener::onPasswordVisibilityToggled
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
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message = state.errorMessage ?: "",
                        leadingIcon = painterResource(Res.drawable.ic_close_circle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .padding(horizontal = 16.dp)
                    )

                }

                LaunchedEffect(state.errorMessage) {
                    delay(3000)
                    listener.clearErrorMessage()
                }
            }


        }

    }

    override fun onEffect(
        effect: LoginScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is LoginScreenUIEffect.NavigateToRegister -> navigator.push(RegisterScreen())
            LoginScreenUIEffect.NavigateToForgotPassword -> navigator.push(ForgetPasswordScreen())
            LoginScreenUIEffect.NavigateToHome -> navigator.push(ForgetPasswordScreen())
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


