package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm_password_label
import mena.identity_presentation.generated.resources.new_password_title
import mena.identity_presentation.generated.resources.ok
import mena.identity_presentation.generated.resources.reset
import mena.identity_presentation.generated.resources.reset_password
import mena.identity_presentation.generated.resources.reset_password_description
import mena.identity_presentation.generated.resources.reset_password_dialog_message
import mena.identity_presentation.generated.resources.reset_password_dialog_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import org.jetbrains.compose.resources.stringResource

class SetNewPasswordScreen() : BaseScreen<
    SetNewPasswordScreenViewModel,
    SetNewPasswordScreenUIState,
    SetNewPasswordScreenUIEffect,
    SetNewPasswordScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: SetNewPasswordScreenUIState, listener: SetNewPasswordScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.reset_password),
                    onClickBack = listener::onClickBack
                )
            },
            overlays = {
                dialog(
                    isVisible = state.isDialogVisible,
                ) {
                    ResetPasswordDialog(
                        isVisible = state.isDialogVisible,
                        onClick = listener::onClickOk,
                    )
                }
            }
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colorScheme.background.surface)
            ) {
                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.new_password_title),
                        subtitle = stringResource(Res.string.reset_password_description),
                    )

                    LabeledInputPassword(
                        password = state.newPassword,
                        isPasswordVisible = state.isNewPasswordVisible,
                        onChangePassword = listener::onChangeNewPassword,
                        onTogglePasswordVisibility = listener::onToggleNewPasswordVisibility,
                        label = stringResource(Res.string.new_password_title),
                        errorMessage = state.newPasswordErrorMessage?.let {
                            stringResource(it)
                        },
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    LabeledInputPassword(
                        password = state.confirmPassword,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onChangePassword = listener::onChangeConfirmPassword,
                        onTogglePasswordVisibility = listener::onToggleConfirmPasswordVisibility,
                        label = stringResource(Res.string.confirm_password_label),
                        errorMessage = state.confirmPasswordErrorMessage?.let {
                            stringResource(it)
                        },
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.reset),
                        onClick = listener::onClickResetPassword,
                        isEnabled = state.isResetEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Theme.spacing._12)
                            .imePadding()
                    )
                }

            }
        }
    }

    override fun onEffect(
        effect: SetNewPasswordScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            SetNewPasswordScreenUIEffect.NavigateBackToLogin -> navigator.push(LoginScreen())

            is SetNewPasswordScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource,
                )
            }
        }
    }
}

@Composable
private fun ScaffoldScope.ResetPasswordDialog(
    isVisible: Boolean,
    onClick: () -> Unit,
) {
    Dialog(
        title = stringResource(Res.string.reset_password_dialog_title),
        message = stringResource(Res.string.reset_password_dialog_message),
        hasDismissButton = true,
        dismissOnClickOutside = false,
        isVisible = isVisible,
        onDismiss = {},
        onCancelClick = onClick::invoke,
        actionButtons = {
            TextButton(
                text = stringResource(Res.string.ok),
                onClick = onClick::invoke,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    )
}