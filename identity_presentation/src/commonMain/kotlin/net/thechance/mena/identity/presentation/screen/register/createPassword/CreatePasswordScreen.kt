package net.thechance.mena.identity.presentation.screen.register.createPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm_password_label
import mena.identity_presentation.generated.resources.create_password_description
import mena.identity_presentation.generated.resources.create_password_title
import mena.identity_presentation.generated.resources.new_password_title
import mena.identity_presentation.generated.resources.register
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.components.PageDescription
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class CreatePasswordScreen : BaseScreen<
        CreatePasswordViewModel,
        CreatePasswordUIState,
        CreatePasswordUIEffect,
        CreatePasswordInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: CreatePasswordUIState,
        listener: CreatePasswordInteractionListener
    ) {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colorScheme.background.surface)
            ) {
                AuthScreenContainer {
                    PageDescription(
                        title = stringResource(Res.string.create_password_title),
                        subtitle = stringResource(Res.string.create_password_description),
                    )

                    LabeledInputPassword(
                        password = state.newPassword,
                        isPasswordVisible = state.isNewPasswordVisible,
                        onChangePassword = listener::onChangeNewPassword,
                        onTogglePasswordVisibility = listener::onToggleNewPasswordVisibility,
                        label = stringResource(Res.string.new_password_title),
                        errorMessage = state.newPasswordErrorMessage?.let { stringResource(it) },
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    LabeledInputPassword(
                        password = state.confirmPassword,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onChangePassword = listener::onChangeConfirmPassword,
                        onTogglePasswordVisibility = listener::onToggleConfirmPasswordVisibility,
                        label = stringResource(Res.string.confirm_password_label),
                        errorMessage = state.confirmPasswordErrorMessage,
                        modifier = Modifier.padding(bottom = Theme.spacing._16)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = stringResource(Res.string.register),
                        onClick = listener::onClickCreatePassword,
                        isEnabled = state.isCreateEnabled,
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
        ErrorSnackBar(
            errorMessage = state.errorMessage?.let { stringResource(it) },
            onDismiss = listener::onClearErrorMessage,
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: CreatePasswordUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            CreatePasswordUIEffect.NavigateBack -> navigator.pop()
        }
    }
}

@Preview
@Composable
fun PreviewCreatePasswordScreen() {
    MenaTheme {
        CreatePasswordScreen().OnRender(
            state = CreatePasswordUIState(
                newPassword = "Password123",
                confirmPassword = "Password123",
                isCreateEnabled = true
            ),
            listener = object : CreatePasswordInteractionListener {
                override fun onChangeNewPassword(password: String) {}
                override fun onChangeConfirmPassword(password: String) {}
                override fun onToggleNewPasswordVisibility() {}
                override fun onToggleConfirmPasswordVisibility() {}
                override fun onClickCreatePassword() {}
                override fun onClickBack() {}
                override fun onClearErrorMessage() {}
            }
        )
    }
}