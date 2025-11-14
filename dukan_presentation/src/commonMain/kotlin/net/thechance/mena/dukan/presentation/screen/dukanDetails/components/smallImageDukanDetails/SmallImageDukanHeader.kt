package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import mena.dukan_presentation.generated.resources.ic_favorite
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmallImageDukanImageAndTitle(
    dukanInfoState: DukanInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = dukanInfoState.imageUrl,
            contentDescription = stringResource(Res.string.dukan_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp)
                .clip(CircleShape)
        )
        Text(
            text = dukanInfoState.name,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SmallImageDukanIconButton(
    icon: Painter,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {}
) {
    Icon(
        painter = icon,
        contentDescription = stringResource(Res.string.dukan_icon),
        tint = iconColor,
        modifier = modifier.clip(CircleShape)
            .background(iconColor.copy(alpha = 0.06f))
            .clickable(
                onClick = onIconClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .padding(vertical = Theme.spacing._12 + Theme.spacing._2, horizontal = 42.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun DukanImageAndTitlePreview() {
    MenaTheme {
        SmallImageDukanImageAndTitle(
            dukanInfoState = fakeDukanInfo,
            modifier = Modifier.padding(Theme.spacing._16)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SmallImageDukanIconButtonPreview() {
    MenaTheme {
        SmallImageDukanIconButton(
            icon = painterResource(Res.drawable.ic_favorite),
            iconColor = Color(fakeDukanInfo.color),
            modifier = Modifier.padding(Theme.spacing._16)
        )
    }
}
