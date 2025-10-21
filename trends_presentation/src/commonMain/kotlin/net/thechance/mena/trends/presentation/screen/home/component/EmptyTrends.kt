package net.thechance.mena.trends.presentation.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.empty_trends_icon
import mena.trends_presentation.generated.resources.ic_empty_trends
import mena.trends_presentation.generated.resources.no_trends_yet
import mena.trends_presentation.generated.resources.no_trends_yet_description
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyTrends(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._24)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_empty_trends),
            contentDescription = stringResource(Res.string.empty_trends_icon),
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 12.dp)
        )

        Text(
            text = stringResource(Res.string.no_trends_yet),
            style = Theme.typography.title.small,
            textAlign = TextAlign.Center,
            color = Theme.colorScheme.shadePrimary,
        )

        Text(
            text = stringResource(Res.string.no_trends_yet_description),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._2)
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