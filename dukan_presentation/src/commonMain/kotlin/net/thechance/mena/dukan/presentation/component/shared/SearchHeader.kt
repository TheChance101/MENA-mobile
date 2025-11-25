package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_delete_search
import mena.dukan_presentation.generated.resources.ic_search
import mena.dukan_presentation.generated.resources.search_in_dukans
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Theme.spacing._16),
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._8),
        modifier = modifier.padding(paddingValues = contentPadding)
    ) {

        AppBarOptionContainer(
            onClick = onBackClick,
            content = {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(resource = Res.string.back_to_main_screen_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }
        )

        TextField(
            modifier = Modifier.weight(1f),
            value = query,
            onValueChanged = onQueryChange,
            hint = stringResource(resource = Res.string.search_in_dukans),
            leadingIcon = painterResource(resource = Res.drawable.ic_search),
            leadingIconTint = Theme.colorScheme.shadeSecondary,
            onTrailingIconClick = onClearClick,
            showTrailingDivider = false,
            trailingIcon = if (query.isNotEmpty())
                painterResource(resource = Res.drawable.ic_delete_search)
            else
                null,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchHeader(
                query = "Search query",
                onQueryChange = {},
                onBackClick = {},
                onClearClick = {}
            )
        }
    }
}