package net.thechance.mena.trends.presentation.shared.component

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_no_connection
import mena.trends_presentation.generated.resources.re_try
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatePlaceholder(
    modifier: Modifier = Modifier,
    isBottomVisible: Boolean = false,
    stateIcon: Painter,
    stateTitle: String,
    stateDescription: String,
    onRetry: () -> Unit = {}
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._24)
    ) {
        Image(
            painter = stateIcon,
            contentDescription = stateTitle,
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = Theme.spacing._12)
        )

        Text(
            text = stateTitle,
            style = Theme.typography.title.small,
            textAlign = TextAlign.Center,
            color = Theme.colorScheme.shadePrimary,
        )

        Text(
            text = stateDescription,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(top = Theme.spacing._2, bottom = Theme.spacing._12)
        )

        if (isBottomVisible) {
            PrimaryButton(
                text = stringResource(Res.string.re_try),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBaseStateComponent() {
    MenaTheme {
        StatePlaceholder(
            stateIcon = painterResource(Res.drawable.ic_no_connection),
            stateTitle = "No Connection",
            stateDescription = "Please check your internet connection and try again.",
            isBottomVisible = false,
            onRetry = {}
        )
    }
}

