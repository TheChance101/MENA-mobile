package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_addresses
import mena.identity_presentation.generated.resources.profile_edit_profile_icon_content_description
import mena.identity_presentation.generated.resources.profile_edit_profile_info
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun SettingItem(
    title: String, leadingIcon: Painter, trailingText: String? = null, onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = {
                    onClick()
                }
            )
            .padding(horizontal = Theme.spacing._12),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(vertical = Theme.spacing._12)
                .size(Theme.spacing._24),
            painter = leadingIcon,
            tint = Theme.colorScheme.primary.primary ,
            contentDescription = stringResource(Res.string.profile_edit_profile_icon_content_description),
        )
        Text(
            text = title,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1f).padding(horizontal = 1.dp)
        )
        trailingText?.let {
            Text(
                text = trailingText,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Theme.spacing._2)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingItem() {
    MenaTheme {
        SettingItem(
            title = stringResource(Res.string.profile_edit_profile_info),
            leadingIcon = painterResource(Res.drawable.ic_addresses),
            trailingText = "Optional",
            onClick = {}
        )
    }
}
