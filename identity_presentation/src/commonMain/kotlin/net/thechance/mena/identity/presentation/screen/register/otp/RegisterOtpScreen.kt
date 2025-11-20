package net.thechance.mena.identity.presentation.screen.register.otp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.did_not_receive_code
import mena.identity_presentation.generated.resources.otp_code
import mena.identity_presentation.generated.resources.register_otp_prompt
import mena.identity_presentation.generated.resources.resend
import mena.identity_presentation.generated.resources.resend_timer
import mena.identity_presentation.generated.resources.validation_code
import mena.identity_presentation.generated.resources.verify
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.OtpInput
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.register.enterName.EnterNameScreen
import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.parameter.parametersOf

class RegisterOtpScreen(
    private val registerUIState: RegisterUIState
) : BaseScreen<
    RegisterOtpViewModel,
    RegisterOtpUIState,
    RegisterOtpUIEffect,
    RegisterOtpInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(registerUIState) }))
    }

    @Composable
    override fun OnRender(
        state: RegisterOtpUIState,
        listener: RegisterOtpInteractionListener
    ) {
        Scaffold {
            AuthScreenContainer {
                PageDescription(
                    title = stringResource(Res.string.validation_code),
                    subtitle = stringResource(
                        Res.string.register_otp_prompt,
                        registerUIState.phoneNumber.localNumber.takeLast(2)
                    )
                )

                Text(
                    text = stringResource(Res.string.otp_code),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Theme.spacing._4)
                )
                OtpInput(
                    otpValue = state.otpValue,
                    onOtpChange = listener::onChangeOtp,
                    otpLength = 6,
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(Res.string.verify),
                    onClick = listener::onClickVerify,
                    isEnabled = state.isVerifyEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Theme.spacing._12, top = Theme.spacing._24)
                )

                AuthPrompt(
                    message = stringResource(Res.string.did_not_receive_code),
                    actionLabel = if (state.isResendEnabled) stringResource(Res.string.resend) else stringResource(
                        Res.string.resend_timer,
                        getTimerMinutes(state.timer),
                        getTimerSeconds(state.timer)
                    ),
                    onActionClick = listener::onClickResend,
                    isEnabled = state.isResendEnabled,
                    modifier = Modifier.imePadding()
                )
            }
        }
    }

    override fun onEffect(
        effect: RegisterOtpUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is RegisterOtpUIEffect.NavigateToEnterName -> {
                navigator.push(EnterNameScreen(phoneNumber = effect.phoneNumber))
            }

            is RegisterOtpUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}

private fun getTimerMinutes(timer: String): Int {
    return timer.toIntOrNull()?.div(60) ?: 0
}

private fun getTimerSeconds(timer: String): String {
    val totalSeconds = timer.toIntOrNull() ?: 0
    return (totalSeconds % 60).toString().padStart(2, '0')
}

@Preview
@Composable
fun PreviewRegisterOtpScreen() {
    MenaTheme {
        RegisterOtpScreen(
            RegisterUIState(
                phoneNumber = PhoneNumber(
                    countryCode = "+964",
                    localNumber = "790123456"
                ),
                countryCode = "+964"
            )
        ).OnRender(
            state = RegisterOtpUIState(
                otpValue = "123456",
                isVerifyEnabled = true,
                timer = "45",
                isResendEnabled = false
            ),
            listener = object : RegisterOtpInteractionListener {
                override fun onClickVerify() {}
                override fun onClickResend() {}
                override fun onChangeOtp(otp: String) {}
            }
        )
    }
}