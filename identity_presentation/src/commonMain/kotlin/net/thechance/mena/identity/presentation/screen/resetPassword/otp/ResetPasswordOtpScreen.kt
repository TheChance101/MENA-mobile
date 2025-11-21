package net.thechance.mena.identity.presentation.screen.resetPassword.otp

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
import mena.identity_presentation.generated.resources.otp_prompt
import mena.identity_presentation.generated.resources.otp_prompt_title
import mena.identity_presentation.generated.resources.resend
import mena.identity_presentation.generated.resources.resend_timer
import mena.identity_presentation.generated.resources.reset_password
import mena.identity_presentation.generated.resources.verify
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthPrompt
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.OtpInput
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword.SetNewPasswordScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class ResetPasswordOtpScreen(
    private val phoneNumber: String,
    private val countryCode: String,
    private val callingCode: String
) : BaseScreen<
    ResetPasswordOtpScreenViewModel,
    ResetPasswordOtpScreenUIState,
    ResetPasswordOtpScreenUIEffect,
    ResetPasswordOtpScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(
            getScreenModel(parameters = {
                parametersOf(phoneNumber, callingCode, countryCode)
            })
        )
    }

    @Composable
    override fun OnRender(
        state: ResetPasswordOtpScreenUIState,
        listener: ResetPasswordOtpScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.reset_password),
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            AuthScreenContainer {
                PageDescription(
                    title = stringResource(Res.string.otp_prompt_title),
                    subtitle = stringResource(Res.string.otp_prompt, phoneNumber.takeLast(2))
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

                val minutes = state.timer.toInt() / 60
                val seconds = (state.timer.toInt() % 60).toString().padStart(2, '0')

                AuthPrompt(
                    message = stringResource(Res.string.did_not_receive_code),
                    actionLabel = if (state.isResendEnabled) stringResource(Res.string.resend) else stringResource(
                        Res.string.resend_timer,
                        minutes,
                        seconds
                    ),
                    onActionClick = listener::onClickResend,
                    isEnabled = state.isResendEnabled,
                    modifier = Modifier.imePadding()
                )
            }
        }
    }

    override fun onEffect(
        effect: ResetPasswordOtpScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            ResetPasswordOtpScreenUIEffect.NavigateBack -> navigator.pop()
            is ResetPasswordOtpScreenUIEffect.NavigateToResetPassword -> {
                navigator.popUntilRoot()
                navigator.push(SetNewPasswordScreen())
            }

            is ResetPasswordOtpScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}
