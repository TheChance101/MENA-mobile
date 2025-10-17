package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.app_name
import mena.identity_presentation.generated.resources.mena_logo
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PageDescription(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.mena_logo),
                    contentDescription = null,
                    modifier = Modifier.size(88.dp)
                )
                Text(
                    text = stringResource(Res.string.app_name),
                    textAlign = TextAlign.Center,
                    style = Theme.typography.appName,
                    color = Theme.colorScheme.shadePrimary
                )
            }
        }
        Text(
            text = title,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)

        )
        Text(
            text = subtitle,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing._24)
        )
    }
}


@Preview
@Composable
fun PageDescriptionPreview() {
    MenaTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),

            ) {
            PageDescription(
                title = "Hi, I'm a title",
                subtitle = "Hi, I'm a subtitle"
            )
        }
    }
}