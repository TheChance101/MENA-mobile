package net.thechance.mena.faith.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.downloaded
import mena.faith_presentation.generated.resources.ic_tick_double_check
import mena.faith_presentation.generated.resources.icon_download
import mena.faith_presentation.generated.resources.icon_play
import mena.faith_presentation.generated.resources.play
import mena.faith_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.utils.extentions.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReciterItem(
    reciter: String,
    recitingType: String,
    isDownloaded: Boolean,
    onPlayClick: () -> Unit = {},
    onDownloadClick: () -> Unit,
    onSelect: () -> Unit = {},
    isSelectReciter: Boolean,
    isDownloadIconVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelectReciter)
        Theme.colorScheme.primary.primary else Theme.colorScheme.background.surfaceLow
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = Theme.spacing._16)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .padding(horizontal = 8.dp)
            .noRippleClickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayButton(
            painterIcon = painterResource(Res.drawable.icon_play),
            contentDescription = stringResource(Res.string.play),
            modifier = Modifier
                .size(size = 40.dp)
                .noRippleClickable(onPlayClick)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = reciter,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(start = Theme.spacing._8)
            )
            RecitersDetails(
                recitingType = recitingType,
                isDownloaded = isDownloaded
            )
        }
        if (!isDownloaded && isDownloadIconVisible) {
            Icon(
                painterResource(Res.drawable.icon_download),
                contentDescription = stringResource(Res.string.success),
                modifier = Modifier
                    .size(size = 20.dp)
                    .clickable(onClick = onDownloadClick)
            )
        }
    }
}

@Composable
private fun RecitersDetails(
    recitingType: String,
    isDownloaded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        Text(
            text = recitingType,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.padding(start = Theme.spacing._8)
        )
        if (isDownloaded) {
            Icon(
                painterResource(Res.drawable.ic_tick_double_check),
                contentDescription = stringResource(Res.string.success),
                modifier = Modifier.size(Theme.spacing._12)
            )

            Text(
                text = stringResource(Res.string.downloaded),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.success
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            ReciterItem(
                reciter = "Muhammad Siddiq Al-Minshawi",
                recitingType = "Teacher - Tajweed",
                isDownloaded = true,
                onSelect = {},
                onDownloadClick = {},
                isSelectReciter = false,
                isDownloadIconVisible = true,
            )
        }
    }
}
