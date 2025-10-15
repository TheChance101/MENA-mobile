package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_icon
import mena.dukan_presentation.generated.resources.dukan_image
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanImageAndTitle(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp)
                .clip(RoundedCornerShape(Theme.radius.full))
        )
        Text(
            text = state.name,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DukanIconButton(
    icon: Painter,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {}
) {
    Icon(
        painter = icon,
        contentDescription = stringResource(Res.string.dukan_icon),
        tint = iconColor,
        modifier = modifier.clip(RoundedCornerShape(Theme.radius.full))
            .background(iconColor.copy(alpha = 0.04f))
            .clickable(
                onClick = onIconClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .padding(vertical = Theme.spacing._12 + Theme.spacing._2, horizontal = 43.dp)
    )
}

@Composable
fun DukanHeaderIcon(
    icon: Painter,
    onIconClick: () -> Unit,
    isBadgeVisible: Boolean = false
) {
    AppBarOptionContainer(
        onClick = { onIconClick() },
        isBadgeVisible = isBadgeVisible
    ) {
        Icon(
            painter = icon,
            contentDescription = stringResource(Res.string.dukan_icon)
        )
    }
}
