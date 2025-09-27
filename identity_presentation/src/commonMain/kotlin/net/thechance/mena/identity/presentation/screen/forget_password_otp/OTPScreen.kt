package net.thechance.mena.identity.presentation.screen.forget_password_otp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.OtpInput
import net.thechance.mena.identity.presentation.components.PageDescription
import org.koin.core.parameter.parametersOf

class OTPScreen(
    private val phoneNumber: String,
    private val countryCode: String
) : BaseScreen<
        OTPScreenModel,
        OTPScreenUIState,
        OTPScreenUIEffect,
        OTPScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(phoneNumber, countryCode) }))
    }


    @Composable
    override fun OnRender(
        state: OTPScreenUIState,
        listener: OTPScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = "Reset password",
                    onBackClicked = listener::onBackClicked
                )
            },
            snackBar = {
                ErrorSnackBar(
                    errorMessage = state.errorMessage,
                    onDismiss = listener::clearErrorMessage,
                )
            }
        ) {
            AuthScreenContainer {
                PageDescription(
                    title = "Enter the verification code",
                    subtitle = "please enter the OTP code that send to your phone number end with **${
                        phoneNumber.takeLast(
                            2
                        )
                    } to reset your password",
                )
                Text(
                    text = "OTP code",
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OtpInput(
                    otpValue = state.otpValue,
                    onOtpChange = listener::onOtpChanged,
                    otpLength = 6,
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Verify",
                    onClick = listener::onVerifyClicked,
                    isEnabled = state.isVerifyEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, top = 24.dp)
                )

                AuthPrompt(
                    message = "Didn't receive the code?",
                    actionLabel = if (state.isResendEnabled) "Resend" else "Resend in ${state.timer.toInt() / 60}:${state.timer.toInt() % 60}",
                    onActionClick = listener::onResendClicked,
                    isEnabled = state.isResendEnabled
                )
            }
        }
    }

    override fun onEffect(
        effect: OTPScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            OTPScreenUIEffect.NavigateBack -> navigator.pop()
            OTPScreenUIEffect.NavigateToResetPassword -> TODO("add reset password screen")
        }
    }
}