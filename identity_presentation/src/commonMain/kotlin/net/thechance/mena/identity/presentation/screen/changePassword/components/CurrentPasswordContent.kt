package net.thechance.mena.identity.presentation.screen.changePassword.components

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
import mena.identity_presentation.generated.resources.`continue`
import mena.identity_presentation.generated.resources.password
import mena.identity_presentation.generated.resources.please_enter_your_password_to_change_it
import mena.identity_presentation.generated.resources.verify_current_password
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreenUIState.CurrentPasswordContentUIState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CurrentPasswordContent(
    state: CurrentPasswordContentUIState,
    isLoading: Boolean,
    onClickContinue: () -> Unit,
    onChangeCurrentPassword: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier.fillMaxSize().verticalScroll(scrollState)) {
        Text(
            text = stringResource(Res.string.verify_current_password),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.please_enter_your_password_to_change_it),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._24)
        )

        LabeledInputPassword(
            password = state.currentPassword,
            isPasswordVisible = state.isCurrentPasswordVisible,
            onChangePassword = onChangeCurrentPassword,
            onTogglePasswordVisibility = onToggleCurrentPasswordVisibility,
            label = stringResource(Res.string.password),
            errorMessage = state.currentPasswordErrorMessage?.let {
                stringResource(it)
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(Res.string.`continue`),
            onClick = onClickContinue,
            isEnabled = state.isContinueEnabled,
            isLoading = isLoading,
            contentPadding = PaddingValues(vertical = 13.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._12, top = Theme.spacing._24)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentPasswordContentPreview() {
    MenaTheme {
        CurrentPasswordContent(
            state = CurrentPasswordContentUIState(),
            isLoading = false,
            onClickContinue = {},
            onChangeCurrentPassword = {},
            onToggleCurrentPasswordVisibility = {},
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
    }
}