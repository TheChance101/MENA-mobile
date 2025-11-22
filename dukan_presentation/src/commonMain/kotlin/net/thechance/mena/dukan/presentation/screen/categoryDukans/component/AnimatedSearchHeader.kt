package net.thechance.mena.dukan.presentation.screen.categoryDukans.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_delete_search
import mena.dukan_presentation.generated.resources.ic_search
import mena.dukan_presentation.generated.resources.search_in_dukans
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.slideHorizontalTransition
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun AnimatedCategorySearchHeader(
    categoryTitle: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchMode: Boolean = false,
    onSearchIconClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._8),
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
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

        AnimatedContent(
            modifier = Modifier.weight(1f),  // <-- makes space available for slide
            targetState = onSearchMode,
            transitionSpec = { slideHorizontalTransition() },
            label = "search-header-animation"
        ) { isSearchable ->

            if (!isSearchable) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = categoryTitle,
                        color = Theme.colorScheme.shadePrimary,
                        style = Theme.typography.title.medium
                    )

                    AppBarOptionContainer(
                        onClick = onSearchIconClick,
                        content = {
                            Icon(
                                painter = painterResource(resource = Res.drawable.ic_search),
                                contentDescription = null,
                                tint = Theme.colorScheme.primary.primary
                            )
                        }
                    )
                }

            } else {
                TextField(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    value = query,
                    onValueChanged = onQueryChange,
                    hint = stringResource(resource = Res.string.search_in_dukans),
                    leadingIcon = painterResource(resource = Res.drawable.ic_search),
                    leadingIconTint = Theme.colorScheme.shadeSecondary,
                    onTrailingIconClick = onClearClick,
                    showTrailingDivider = false,
                    trailingIcon = if (query.isNotEmpty())
                        painterResource(resource = Res.drawable.ic_delete_search)
                    else null,
                )
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedCategorySearchHeader(
                query = "Search query",
                onQueryChange = {},
                onClearClick = {},
                onBackClick = {},
                categoryTitle = "category"
            )
        }
    }
}