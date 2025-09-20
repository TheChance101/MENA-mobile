package net.thechance.mena.designsystem.presentation.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.no_connection_retry
import mena.design_system.generated.resources.no_connection_title
import mena.design_system.generated.resources.no_wifi
import mena.design_system.generated.resources.no_wifi_content_description
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Container(
    painter: Painter?,
    painterContentDescription: String?,
    title: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    isLoading: Boolean = false,
    titleColor: Color = Theme.colorScheme.shadePrimary,
    messageColor: Color = Theme.colorScheme.shadeSecondary,
    onButtonClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            painter?.let { imagePainter ->
                Image(
                    painter = imagePainter,
                    contentDescription = painterContentDescription
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    style = Theme.typography.title.small,
                    color = titleColor
                )
                message?.let {
                    Text(
                        text = it,
                        style = Theme.typography.body.small,
                        color = messageColor
                    )
                }
            }
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                text = buttonText,
                isLoading = isLoading,
                onClick = onButtonClick
            )
        }
    }
}

@Preview
@Composable
private fun NoConnectionContainerPreview() {
    MenaTheme {
        Container(
            painter = painterResource(Res.drawable.no_wifi),
            painterContentDescription = stringResource(Res.string.no_wifi_content_description),
            title = stringResource(Res.string.no_connection_title),
            buttonText = stringResource(Res.string.no_connection_retry),
        )
    }
}