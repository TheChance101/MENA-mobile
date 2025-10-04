package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark
import mena.faith_presentation.generated.resources.copy
import mena.faith_presentation.generated.resources.ic_all_bookmark
import mena.faith_presentation.generated.resources.ic_copy
import mena.faith_presentation.generated.resources.ic_link_forward
import mena.faith_presentation.generated.resources.link_forward
import mena.faith_presentation.generated.resources.send_to
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AyahActionButtons(
    onBookmarkClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(Theme.spacing._16)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(
                vertical = Theme.spacing._12,
                horizontal = Theme.spacing._8
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconTextButton(
            icon = painterResource(Res.drawable.ic_link_forward),
            contentDescription = stringResource(Res.string.link_forward),
            text = stringResource(Res.string.send_to),
            onClick = onShareClick
        )
        VerticalDivider()

        IconTextButton(
            icon = painterResource(Res.drawable.ic_all_bookmark),
            contentDescription = stringResource(Res.string.bookmark),
            text = stringResource(Res.string.bookmark),
            onClick = onBookmarkClick
        )
        VerticalDivider()

        IconTextButton(
            icon = painterResource(Res.drawable.ic_copy),
            contentDescription = stringResource(Res.string.copy),
            text = stringResource(Res.string.copy),
            onClick = onCopyClick
        )
    }
}

@Composable
private fun IconTextButton(
    icon: Painter,
    text: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            tint = Theme.colorScheme.primary.primary,
            contentDescription = contentDescription,
            modifier = Modifier.clickable(
                indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            })
        Text(
            text = text,
            color = Theme.colorScheme.primary.primary,
            style = Theme.typography.label.small
        )
    }
}

@Composable
private fun VerticalDivider(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    color: Color = Theme.colorScheme.stroke,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier = modifier
            .width(thickness)
            .height(height)
            .background(color)
    )
}