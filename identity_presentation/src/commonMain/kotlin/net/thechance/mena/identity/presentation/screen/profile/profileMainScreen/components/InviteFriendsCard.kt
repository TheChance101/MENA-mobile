package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_invite_friends
import mena.identity_presentation.generated.resources.profile_invite_friends_icon_content_description
import mena.identity_presentation.generated.resources.profile_invite_friends_subtitle
import mena.identity_presentation.generated.resources.profile_invite_friends_title
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun InviteFriendsCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = Theme.spacing._24)
            .fillMaxWidth()
            .clip(SquircleShape(radius = Theme.radius.lg))
            .background(
                Theme.colorScheme.background.surfaceHigh
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(Theme.spacing._8),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clip(SquircleShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._12)
                .size(Theme.spacing._24),
            painter = painterResource(Res.drawable.ic_invite_friends),
            tint = Theme.colorScheme.shadePrimary,
            contentDescription = stringResource(Res.string.profile_invite_friends_icon_content_description),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Text(
                text = stringResource(Res.string.profile_invite_friends_title),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.profile_invite_friends_subtitle),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
fun PreviewInviteFriendsCard() {
    MenaTheme {
        InviteFriendsCard(onClick = {})
    }
}
