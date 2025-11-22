package net.thechance.mena.identity.presentation.screen.contactUs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
internal fun ContactCard(
    icon: DrawableResource,
    title: StringResource,
    info: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.radius.lg))
            .background(
                Theme.colorScheme.background.surfaceLow,
                SquircleShape(Theme.radius.lg)
            )
            .clickable(
                enabled = info.isNotBlank(),
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClick
            )
            .padding(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(SquircleShape(Theme.radius.sm))
                .background(
                    Theme.colorScheme.background.surfaceHigh,
                    SquircleShape(Theme.radius.sm)
                )
                .padding(Theme.spacing._8)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Theme.colorScheme.primary.primary
            )
        }

        Column(
            modifier = Modifier
                .padding(start = Theme.spacing._8)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Text(
                text = stringResource(title),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary,
            )

            Text(
                text = info,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
            )
        }
    }
}