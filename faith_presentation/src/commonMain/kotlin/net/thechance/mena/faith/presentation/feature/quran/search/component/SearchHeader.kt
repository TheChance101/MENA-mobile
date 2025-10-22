package net.thechance.mena.faith.presentation.feature.quran.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.ic_clear
import mena.faith_presentation.generated.resources.ic_outline_search
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchHeader(
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    clearQuery: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .size(20.dp),
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

@Preview
@Composable
private fun SearchHeaderWithQueryPreview() {
    QuranTheme {
        SearchHeader(
            query = "Allah",
            hint = "Search in Quran...",
            onQueryChange = {},
            onBackClick = {},
            clearQuery = {}
        )
    }
}
