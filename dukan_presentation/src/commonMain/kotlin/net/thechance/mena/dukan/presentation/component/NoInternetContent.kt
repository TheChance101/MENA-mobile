package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ellipse_wifi_1
import mena.dukan_presentation.generated.resources.ellipse_wifi_2
import mena.dukan_presentation.generated.resources.ellipse_wifi_3
import mena.dukan_presentation.generated.resources.ellipse_wifi_4
import mena.dukan_presentation.generated.resources.ic_alert_circle
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.no_internet_title
import mena.dukan_presentation.generated.resources.retry_button
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoInternetContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = Theme.spacing._16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WifiImage(
            modifier = Modifier
                .padding(bottom = Theme.spacing._12),
        )
        Text(
            modifier = Modifier
                .padding(bottom = Theme.spacing._2),
            text = stringResource(Res.string.no_internet_title),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
        )
        Text(
            text = stringResource(Res.string.no_internet_message),
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .heightIn(min = 48.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.retry_button),
            onClick = { onRetry() },
            contentPadding = PaddingValues(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._16
            )
        )
    }
}

@Composable
private fun WifiImage(
    modifier: Modifier = Modifier
) {
    val black = Color(0xFF000000)
    val wifiImages = listOf(
        Res.drawable.ellipse_wifi_4 to 4.33.dp,
        Res.drawable.ellipse_wifi_3 to 28.52.dp,
        Res.drawable.ellipse_wifi_2 to 49.12.dp,
        Res.drawable.ellipse_wifi_1 to 67.27.dp
    )
    Box(
        modifier = modifier
            .size(width = 128.dp, height = 98.dp)
    ) {
        wifiImages.forEachIndexed { index, (drawable, topPadding) ->
            Image(
                modifier = Modifier
                    .padding(top = topPadding)
                    .align(Alignment.TopCenter),
                painter = painterResource(drawable),
                contentDescription = null
            )
        }

        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter)
                .background(black)
        )

        Image(
            modifier = Modifier
                .size(33.5.dp)
                .align(Alignment.TopStart),
            painter = painterResource(Res.drawable.ic_alert_circle),
            contentDescription = null,
            colorFilter = ColorFilter.tint(black)
        )
    }
}

@Preview
@Composable
private fun NoInternetContentPreview() {
    MenaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoInternetContent(onRetry = {})
        }
    }
}