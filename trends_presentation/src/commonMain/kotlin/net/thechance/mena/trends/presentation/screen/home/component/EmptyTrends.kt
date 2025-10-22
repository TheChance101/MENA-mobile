package net.thechance.mena.trends.presentation.screen.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_empty_trends
import mena.trends_presentation.generated.resources.no_trends_description
import mena.trends_presentation.generated.resources.no_trends_title
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.trends.presentation.shared.component.StatePlaceholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyTrends(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            modifier = Modifier,
            isBottomVisible = false,
            stateIcon = painterResource(Res.drawable.ic_empty_trends),
            stateTitle = stringResource(Res.string.no_trends_title),
            stateDescription = stringResource(Res.string.no_trends_description)
        )
    }
}

@Preview
@Composable
fun EmptyTrendsPreview() {
    MenaTheme {
        EmptyTrends()
    }
}