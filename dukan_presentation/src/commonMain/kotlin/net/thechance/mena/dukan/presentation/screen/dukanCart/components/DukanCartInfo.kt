package net.thechance.mena.dukan.presentation.screen.dukanCart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.arrow_right_icon
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.from_dukan
import mena.dukan_presentation.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun DukanCartInfo(
    dukanName: String,
    dukanImageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.background.surface)
            .padding(bottom = Theme.spacing._4)
            .clip(SquircleShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
            )
            .clickable(onClick = { onClick() }, indication = null, interactionSource = null)
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        AsyncImage(
            model = dukanImageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Text(
                text = stringResource(Res.string.from_dukan),
                style = Theme.typography.label.extraSmall,
                color = Theme.colorScheme.shadeSecondary,
            )
            Text(
                text = dukanName,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
            )
        }
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = stringResource(Res.string.arrow_right_icon),
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(24.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surface)
                .padding(Theme.spacing._2 + Theme.spacing._4)
        )
    }
}

@Composable
fun DukanCartInfoSkeleton() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Theme.colorScheme.background.surface)
            .padding(bottom = Theme.spacing._4)
            .clip(SquircleShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
            )
            .padding(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceHigh)
        )
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)) {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 16.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
            Box(
                modifier = Modifier
                    .size(width = 88.dp, height = 16.dp)
                    .clip(SquircleShape(Theme.radius.sm))
                    .background(Theme.colorScheme.background.surfaceHigh)
            )
        }
    }
}