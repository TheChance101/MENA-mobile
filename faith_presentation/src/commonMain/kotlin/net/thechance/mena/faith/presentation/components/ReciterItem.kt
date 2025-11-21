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
import mena.faith_presentation.generated.resources.delete
import mena.faith_presentation.generated.resources.downloaded
import mena.faith_presentation.generated.resources.ic_delete
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
    reciterId: Int,
    reciter: String,
    recitingType: String,
    isDownloaded: Boolean = false,
    isDownloadIconVisible: Boolean = false,
    isSwipeable: Boolean,
    onDownloadClick: () -> Unit = {},
    onSelect: () -> Unit = {},
    isSelectReciter: Boolean = false,
    onDelete: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    SwappableCard(
        isSwipeable = isSwipeable,
        id = reciterId,
        onClick = { onDelete(reciterId) },
        backgroundIcon = painterResource(Res.drawable.ic_delete),
        contentDescription = stringResource(Res.string.delete),
        cardContent = { contentModifier ->
            CardContent(
                reciter = reciter,
                recitingType = recitingType,
                isDownloaded = isDownloaded,
                modifier = contentModifier,
                onDownloadClick = onDownloadClick,
                onSelect = onSelect,
                isSelectReciter = isSelectReciter,
                isDownloadIconVisible = isDownloadIconVisible
            )
        },
        modifier = modifier
            .padding(horizontal = Theme.spacing._16)
            .padding(bottom = Theme.spacing._8)
    )
}

@Composable
private fun CardContent(
    reciter: String,
    recitingType: String,
    isDownloaded: Boolean,
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
            .padding(Theme.spacing._8)
            .noRippleClickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        PlayButton(
            painterIcon = painterResource(Res.drawable.icon_play),
            contentDescription = stringResource(Res.string.play),
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
                modifier = Modifier.size(size = 20.dp)
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
            color = Theme.colorScheme.shadeSecondary
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
            reciterId = 1,
            reciter = "Muhammad Siddiq Al-Minshawi",
            recitingType = "Teacher - Tajweed",
            isDownloaded = true,
            onSelect = {},
            onDownloadClick = {},
            isSelectReciter = false,
            isSwipeable = true,
            isDownloadIconVisible =  true,
            onDelete = {}
        )
    }
    }
}
