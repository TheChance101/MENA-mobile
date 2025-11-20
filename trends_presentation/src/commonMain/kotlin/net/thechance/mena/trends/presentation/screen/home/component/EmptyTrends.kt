package net.thechance.mena.trends.presentation.screen.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.empty_trends_description
import mena.trends_presentation.generated.resources.empty_trends_title
import mena.trends_presentation.generated.resources.ic_empty_trends
import mena.trends_presentation.generated.resources.ic_empty_trends_dark
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.trends.presentation.navigation.LocalDarkTheme
import net.thechance.mena.trends.presentation.shared.component.StatePlaceholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyTrends(modifier: Modifier = Modifier, isScrollable: Boolean = true) {
    val icon = if (LocalDarkTheme.current)
        painterResource(Res.drawable.ic_empty_trends_dark)
    else
        painterResource(Res.drawable.ic_empty_trends)

    StatePlaceholder(
        icon = icon,
        title = stringResource(Res.string.empty_trends_title),
        description = stringResource(Res.string.empty_trends_description),
        isScrollable = isScrollable,
        modifier = modifier
    )
}

@Preview
@Composable
private fun EmptyTrendsPreview() {
    MenaTheme {
        EmptyTrends()
    }
}