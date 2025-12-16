package net.thechance.mena.identity.presentation.screen.profile.changePassword.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm_password_label
import mena.identity_presentation.generated.resources.enter_your_new_password
import mena.identity_presentation.generated.resources.new_password_title
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.screen.profile.changePassword.ChangePasswordScreenInteractionListener
import net.thechance.mena.identity.presentation.screen.profile.changePassword.ChangePasswordScreenUIState.NewPasswordContentUIState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NewPasswordContent(
    state: NewPasswordContentUIState,
    isLoading: Boolean,
    listener: ChangePasswordScreenInteractionListener,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier.fillMaxSize().verticalScroll(scrollState)) {

        Text(
            text = stringResource(Res.string.enter_your_new_password),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.fillMaxWidth()

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
            modifier = Modifier.padding(bottom = Theme.spacing._16, top = Theme.spacing._24)
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
            text = stringResource(Res.string.save),
            onClick = listener::onClickSave,
            isEnabled = state.isSaveEnabled,
            isLoading = isLoading,
            contentPadding = PaddingValues(vertical = 13.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = Theme.spacing._12, top = Theme.spacing._24)
                .imePadding()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewPasswordContentPreview() {

    val listener = object : ChangePasswordScreenInteractionListener {
        override fun onClickBack() {}

        override fun onClickContinue() {}

        override fun onClickSave() {}

        override fun onChangeCurrentPassword(newValue: String) {}

        override fun onChangeNewPassword(newValue: String) {}

        override fun onChangeConfirmPassword(newValue: String) {}

        override fun onToggleCurrentPasswordVisibility() {}

        override fun onToggleNewPasswordVisibility() {}

        override fun onToggleConfirmPasswordVisibility() {}

    }
    MenaTheme {
        NewPasswordContent(
            state = NewPasswordContentUIState(),
            isLoading = false,
            listener = listener,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
    }
}