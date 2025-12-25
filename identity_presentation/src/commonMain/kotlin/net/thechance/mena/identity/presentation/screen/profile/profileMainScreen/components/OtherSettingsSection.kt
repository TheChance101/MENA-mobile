package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_contact_us
import mena.identity_presentation.generated.resources.ic_privacy_and_policies
import mena.identity_presentation.generated.resources.profile_contact_us
import mena.identity_presentation.generated.resources.profile_other_header
import mena.identity_presentation.generated.resources.profile_privacy_and_policy
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OtherSettingsSection(
    onPrivacyAndPolicyClicked: () -> Unit,
    onContactUsClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = Theme.spacing._24),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.profile_other_header),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
        )
        SettingItem(
            title = stringResource(Res.string.profile_privacy_and_policy),
            leadingIcon = painterResource(Res.drawable.ic_privacy_and_policies),
            onClick = onPrivacyAndPolicyClicked,
        )
        SettingItem(
            title = stringResource(Res.string.profile_contact_us),
            leadingIcon = painterResource(Res.drawable.ic_contact_us),
            onClick = onContactUsClicked,
        )
    }
}

@Preview
@Composable
fun PreviewOtherSettingsSection() {
    MenaTheme {
        OtherSettingsSection({}, {})
    }
}
