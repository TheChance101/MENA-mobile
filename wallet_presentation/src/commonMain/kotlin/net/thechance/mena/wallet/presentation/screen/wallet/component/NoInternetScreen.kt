package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.internet_issue
import mena.wallet_presentation.generated.resources.no_internet_description
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoInternetScreen(
    onRetryClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    NoInternetContent(
        onRetryClicked = onRetryClicked,
        modifier = modifier
    )
}


@Composable
fun NoInternetContent(
    onRetryClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp), verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.internet_issue),
                    contentDescription = null,
                    modifier = Modifier
                        .height(97.dp)
                )
                Text(
                    text = stringResource(Res.string.no_internet_title),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.no_internet_description),
                    style = Theme.typography.label.extraSmall,
                    color = Theme.colorScheme.shadeSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            onRetryClicked?.let {
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    text = stringResource(Res.string.retry),
                    onClick = it,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(48.dp),

                )
            }
        }
    }
}


@Preview
@Composable
private fun NoInternetScreen2Preview() {
    NoInternetScreen(
        onRetryClicked = {}
    )
}
