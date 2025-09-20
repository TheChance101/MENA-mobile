package net.thechance.mena.designsystem.presentation.container

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.no_connection_message
import mena.design_system.generated.resources.no_connection_retry
import mena.design_system.generated.resources.no_connection_title
import mena.design_system.generated.resources.no_wifi
import mena.design_system.generated.resources.no_wifi_content_description
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoConnectionContainer(
    isRetry: Boolean,
    modifier: Modifier = Modifier,
    onRefreshClicked: () -> Unit = {}
) {
    Container(
        modifier = modifier,
        painter = painterResource(Res.drawable.no_wifi),
        painterContentDescription = stringResource(Res.string.no_wifi_content_description),
        title = stringResource(Res.string.no_connection_title),
        message = stringResource(Res.string.no_connection_message),
        buttonText = stringResource(Res.string.no_connection_retry),
        onButtonClick = onRefreshClicked,
        isLoading = isRetry
    )
}

@Preview
@Composable
private fun NoConnectionContainerPreview() {
    MenaTheme {
        var isRetry by remember { mutableStateOf(false) }

        NoConnectionContainer(
            isRetry = isRetry,
            onRefreshClicked = {
                isRetry = true
            },
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = null
            ) {
                isRetry = false
            }
        )
    }
}
