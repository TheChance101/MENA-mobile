package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_no_connection
import mena.trends_presentation.generated.resources.ic_no_connection_dark
import mena.trends_presentation.generated.resources.no_connection_description
import mena.trends_presentation.generated.resources.no_connection_title
import mena.trends_presentation.generated.resources.re_try
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.trends.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoConnection(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    val icon = if (LocalDarkTheme.current)
        painterResource(Res.drawable.ic_no_connection_dark)
    else
        painterResource(Res.drawable.ic_no_connection)

    StatePlaceholder(
        icon = icon,
        title = stringResource(Res.string.no_connection_title),
        description = stringResource(Res.string.no_connection_description),
        bottomContent = {
            PrimaryButton(
                text = stringResource(Res.string.re_try),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoConnection() {
    MenaTheme {
        NoConnection {
        }
    }
}
