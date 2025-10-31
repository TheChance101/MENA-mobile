package net.thechance.mena.identity.presentation.screen.changePassword.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.`continue`
import mena.identity_presentation.generated.resources.password
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.LabeledInputPassword
import net.thechance.mena.identity.presentation.screen.changePassword.CurrentPasswordContentUIState
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
){

    Column(modifier = modifier.fillMaxWidth()){

        Text(
            text = "Verify current password",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._24)

        )

        Text(
            text = "Please enter your password to change it",
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
            errorMessage = state.currentPasswordErrorMessage,
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
                .padding(bottom = Theme.spacing._12)
                .imePadding()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentPasswordContentPreview(){
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