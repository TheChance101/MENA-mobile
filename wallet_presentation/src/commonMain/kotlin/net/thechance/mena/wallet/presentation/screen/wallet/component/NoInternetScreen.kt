package net.thechance.mena.wallet.presentation.screen.wallet.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoInternetScreen(
    onRetryClicked: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    noInternetScreenContent(
        onRetryClicked = onRetryClicked,
        modifier = modifier
    )
}

@Composable
private fun noInternetScreenContent(
    onRetryClicked: (() -> Unit),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        noInternetIllustration()
        noInternetTextSection()
        noInternetRetryButton(onRetryClicked)
    }
}

@Composable
private fun noInternetIllustration() {
    Image(
        painter = painterResource(Res.drawable.internet_issue),
        contentDescription = null,
        modifier = Modifier.height(97.dp)
    )
}

@Composable
private fun noInternetTextSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        noInternetTitle()
        noInternetDescription()
    }
}

@Composable
private fun noInternetTitle() {
    Text(
        text = stringResource(Res.string.no_internet_title),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
private fun noInternetDescription() {
    Text(
        text = stringResource(Res.string.no_internet_description),
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary,
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
    )
}

@Composable
private fun noInternetRetryButton(onRetryClicked: (() -> Unit)) {
    PrimaryButton(
        text = stringResource(Res.string.retry),
        onClick = onRetryClicked,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
    )
}

@Preview
@Composable
private fun noInternetScreenPreview() {
    MenaTheme {
        noInternetScreenContent(
            onRetryClicked = {}
        )
    }
}