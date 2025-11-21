package net.thechance.mena.faith.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_search
import mena.faith_presentation.generated.resources.reciters
import mena.faith_presentation.generated.resources.search_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TilawahTopBar(onSearchClick: () -> Unit, onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(Res.string.reciters),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16, vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.arrow_left)
            )
        },
        onLeadingClick = onBackClick,
        trailingContent = { SearchIcon(onSearchClick) }
    )
}

@Composable
private fun SearchIcon(onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(onClick = onSearchClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = stringResource(Res.string.search_icon)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            TilawahTopBar(
                onSearchClick = {},
                onBackClick = {},
            )
        }
    }
}