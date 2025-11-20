package net.thechance.mena.identity.presentation.screen.editProfile.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_delete_address
import mena.identity_presentation.generated.resources.ic_lock
import mena.identity_presentation.generated.resources.logout
import mena.identity_presentation.generated.resources.profile_settings
import mena.identity_presentation.generated.resources.delete_account
import mena.identity_presentation.generated.resources.ic_delete_user
import mena.identity_presentation.generated.resources.ic_logout
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun ScaffoldScope.ProfileSettingsDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onClickLogout: () -> Unit,
    onClickDeleteAccount: () -> Unit,
) {
    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismiss,
        onCancelClick = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = Theme.spacing._12)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.profile_settings),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
            )

            SettingsOption(
                modifier = Modifier.padding(top = Theme.spacing._24),
                painter = painterResource(Res.drawable.ic_logout),
                title = stringResource(Res.string.logout),
                onClick = {
                    onClickLogout()
                    onDismiss()
                }
            )
            SettingsOption(
                modifier = Modifier.padding(top = Theme.spacing._8),
                painter = painterResource(Res.drawable.ic_delete_user),
                title = stringResource(Res.string.delete_account),
                onClick = {
                    onClickDeleteAccount()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun SettingsOption(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClick
            )
            .padding(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painter,
            contentDescription = null,
            tint = Theme.colorScheme.error
        )

        Text(
            modifier = Modifier.padding(start = Theme.spacing._8),
            text = title,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.error
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ProfileSettingsDialogPreview() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        Scaffold(
            overlays = {
                dialog(isVisible = showDialog) {
                    ProfileSettingsDialog(
                        isVisible = showDialog,
                        onDismiss = { showDialog = false },
                        onClickLogout = { showDialog = false },
                        onClickDeleteAccount = { showDialog = false }
                    )
                }
            }
        ) {
            TextButton(
                text = "Show Profile Settings",
                onClick = { showDialog = true }
            )
        }
    }
}