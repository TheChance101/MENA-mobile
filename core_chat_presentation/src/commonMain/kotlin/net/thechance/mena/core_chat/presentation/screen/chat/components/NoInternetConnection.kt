package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_no_internet_connected
import mena.core_chat_presentation.generated.resources.no_internet_connected
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoInternetConnection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = Theme.spacing._8,
                vertical = Theme.spacing._12
            )
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_no_internet_connected),
            contentDescription = stringResource(Res.string.no_internet_connected)
        )
        Text(
            modifier = Modifier
                .padding(top = Theme.spacing._4),
            text = stringResource(Res.string.no_internet_connected),
            color = Theme.colorScheme.error,
            style = Theme.typography.label.small,
            textAlign = TextAlign.Center
        )


    }
}

@Preview
@Composable
private fun NoInternetConnectionPreview() {
    MenaTheme {
        Column(
            modifier = Modifier
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoInternetConnection()
        }
    }
}