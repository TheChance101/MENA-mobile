package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_addresses
import mena.identity_presentation.generated.resources.ic_edit_profile_info
import mena.identity_presentation.generated.resources.ic_password_lock
import mena.identity_presentation.generated.resources.profile_account_settings_header
import mena.identity_presentation.generated.resources.profile_addresses
import mena.identity_presentation.generated.resources.profile_change_password
import mena.identity_presentation.generated.resources.profile_edit_profile_info
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountSettingsSection(
    onEditProfileInfoClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onAddressesClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = Theme.spacing._24),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.profile_account_settings_header),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary,
        )
        SettingItem(
            title = stringResource(Res.string.profile_edit_profile_info),
            leadingIcon = painterResource(Res.drawable.ic_edit_profile_info),
            onClick = onEditProfileInfoClicked,
        )
        SettingItem(
            title = stringResource(Res.string.profile_change_password),
            leadingIcon = painterResource(Res.drawable.ic_password_lock),
            onClick = onChangePasswordClicked,
        )
        SettingItem(
            title = stringResource(Res.string.profile_addresses),
            leadingIcon = painterResource(Res.drawable.ic_addresses),
            onClick = onAddressesClicked,
        )
    }
}

@Preview
@Composable
fun PreviewAccountSettingsSection() {
    MenaTheme {
        AccountSettingsSection(
            {}, {}, {}
        )
    }
}
