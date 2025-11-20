package net.thechance.mena.faith.presentation.feature.quran.reciter.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_clear
import mena.faith_presentation.generated.resources.ic_outline_search
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchReciter(
    title: String,
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    clearQuery: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchMode by remember { mutableStateOf(false) }

    if (isSearchMode) {
        SearchModeBar(
            query = query,
            hint = hint,
            onQueryChange = onQueryChange,
            clearQuery = clearQuery,
            onExitSearch = { isSearchMode = false },
            modifier = modifier
        )
    } else {
        NormalAppBar(
            title = title,
            onBackClick = onBackClick,
            onSearchClick = { isSearchMode = true },
            modifier = modifier
        )
    }
}


@Composable
private fun SearchModeBar(
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    clearQuery: () -> Unit,
    onExitSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable {
                    onExitSearch()
                    clearQuery()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back)
            )
        }

        TextField(
            modifier = Modifier.weight(1f),
            value = query,
            hint = hint,
            onValueChanged = onQueryChange,
            leadingIcon = painterResource(Res.drawable.ic_outline_search),
            leadingIconTint = Theme.colorScheme.shadeSecondary,
            trailingIcon = if (query.isNotBlank()) painterResource(Res.drawable.ic_clear) else null,
            onTrailingIconClick = clearQuery
        )
    }
}

@Composable
private fun NormalAppBar(
    title: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        modifier = modifier,
        title = title,
        leadingContent = {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back)
            )
        },
        onLeadingClick = onBackClick,
        trailingContent = {
            AppBarOptionContainer(
                onClick = onSearchClick
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.ic_outline_search),
                    contentDescription = "Search"
                )
            }
        }
    )
}


@Preview
@Composable
private fun SearchHeaderPreview() {
    MenaTheme {
        QuranTheme {
            SearchReciter(
                query = "",
                hint = "Search in Reciter...",
                onQueryChange = {},
                clearQuery = {},
                onBackClick = {},
                title = "Reciter"
            )
        }
    }
}

