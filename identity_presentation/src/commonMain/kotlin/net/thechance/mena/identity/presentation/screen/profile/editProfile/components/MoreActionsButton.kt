package net.thechance.mena.identity.presentation.screen.profile.editProfile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.more_horizontal
import mena.identity_presentation.generated.resources.options
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
internal fun MoreActionsButton(
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication(),
                onClick = onClick
            )
            .padding(10.dp)
            .size(20.dp),
        tint = Theme.colorScheme.shadePrimary,
        painter = painterResource(Res.drawable.more_horizontal),
        contentDescription = stringResource(Res.string.options),
    )
}