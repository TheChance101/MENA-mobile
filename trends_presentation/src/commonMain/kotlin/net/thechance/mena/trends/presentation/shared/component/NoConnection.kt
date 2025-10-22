package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_no_connection
import mena.trends_presentation.generated.resources.no_connection_description
import mena.trends_presentation.generated.resources.no_connection_title
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoConnection(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            modifier = Modifier,
            isBottomVisible = true,
            stateIcon = painterResource(Res.drawable.ic_no_connection),
            stateTitle = stringResource(Res.string.no_connection_title),
            stateDescription = stringResource(Res.string.no_connection_description),
            onRetry = onRetry
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoConnection() {
    MenaTheme {
        NoConnection {
        }
    }
}
