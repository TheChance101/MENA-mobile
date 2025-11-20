package net.thechance.mena.faith.presentation.feature.quran.search.ayah.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_empty_search
import mena.faith_presentation.generated.resources.ic_search_warning
import mena.faith_presentation.generated.resources.icon_shadow
import mena.faith_presentation.generated.resources.no_results_found_subtitle
import mena.faith_presentation.generated.resources.no_results_found_title
import mena.faith_presentation.generated.resources.shadow
import mena.faith_presentation.generated.resources.start_searching_title
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SearchEmptyState(
    isStartState: Boolean,
    isResultsState: Boolean,
    subtitle: StringResource,
    modifier: Modifier = Modifier
) {
    if (!(isStartState || isResultsState)) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateIcon(
            isStartState = isStartState,
        )

        Text(
            text = stringResource(getTitleResource(isStartState)),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(getSubtitleResource(isStartState, startStateSubtitle = subtitle)),
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center,
            color = Theme.colorScheme.shadeSecondary,
        )
    }
}


@Composable
private fun EmptyStateIcon(
    isStartState: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = modifier.align(Alignment.BottomCenter),
            painter = painterResource(Res.drawable.shadow),
            contentDescription = stringResource(Res.string.icon_shadow),
        )
        Image(
            modifier = modifier.padding(bottom = Theme.spacing._12),
            painter = painterResource(getIconResource(isStartState)),
            contentDescription = stringResource(getTitleResource(isStartState))
        )
    }
}

private fun getIconResource(isStartState: Boolean): DrawableResource {
    return if (isStartState) Res.drawable.ic_empty_search else Res.drawable.ic_search_warning
}

private fun getTitleResource(isStartState: Boolean): StringResource {
    return if (isStartState) Res.string.start_searching_title else Res.string.no_results_found_title
}

private fun getSubtitleResource(
    isStartState: Boolean,
    startStateSubtitle: StringResource
): StringResource {
    return if (isStartState) startStateSubtitle else Res.string.no_results_found_subtitle
}

@Preview
@Composable
private fun SearchEmptyStateStartPreview() {
    MenaTheme {
        QuranTheme {
            SearchEmptyState(
                isStartState = true,
                isResultsState = false,
                modifier = Modifier.fillMaxSize(),
                subtitle = Res.string.start_searching_title
            )
        }
    }
}

@Preview
@Composable
private fun SearchEmptyStateNoResultsPreview() {
    MenaTheme {
        QuranTheme {
            SearchEmptyState(
                isStartState = false,
                isResultsState = true,
                modifier = Modifier.fillMaxSize(),
                subtitle = Res.string.start_searching_title
            )
        }
    }
}
