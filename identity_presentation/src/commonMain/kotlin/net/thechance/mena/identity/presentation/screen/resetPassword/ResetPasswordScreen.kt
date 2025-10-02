package net.thechance.mena.identity.presentation.screen.resetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm_password_label
import mena.identity_presentation.generated.resources.new_password_title
import mena.identity_presentation.generated.resources.reset
import mena.identity_presentation.generated.resources.reset_password
import mena.identity_presentation.generated.resources.reset_password_description
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabelInputPassword
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class ResetPasswordScreen(
    private val phoneNumber: String,
    private val callingCode: String
) :
    BaseScreen<ResetPasswordScreenViewModel,
            ResetPasswordScreenUIState,
            ResetPasswordScreenUIEffect,
            ResetPasswordScreenInteractionListener>()
    {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(phoneNumber, callingCode) }))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun OnRender(
        state: ResetPasswordScreenUIState, listener: ResetPasswordScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.reset_password),
                    onBackClicked = listener::onClickBack
                )
            })
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colorScheme.background.surface)
            ) {
                AuthScreenContainer() {
                    PageDescription(
                        title = stringResource(Res.string.new_password_title),
                        subtitle = stringResource(Res.string.reset_password_description),
                    )

                    LabelInputPassword(
                        password = state.newPassword,
                        isPasswordVisible = state.isNewPasswordVisible,
                        onChangePassword = listener::onChangeNewPassword,
                        onTogglePasswordVisibility = listener::onToggleNewPasswordVisibility,
                        label = stringResource(Res.string.new_password_title),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LabelInputPassword(
                        password = state.confirmPassword,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onChangePassword = listener::onChangeConfirmPassword,
                        onTogglePasswordVisibility = listener::onToggleConfirmPasswordVisibility,
                        label = stringResource(Res.string.confirm_password_label),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.reset),
                        onClick = listener::onClickResetPassword,
                        isEnabled = state.isResetEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = Theme.spacing._12),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Theme.spacing._24)
                    )
                }
            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage,
            onDismiss = listener::onClearErrorMessage,
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: ResetPasswordScreenUIEffect, navigator: Navigator
    ) {
        when (effect) {
            ResetPasswordScreenUIEffect.NavigateBackToLogin -> navigator.push(LoginScreen())
        }
    }
}