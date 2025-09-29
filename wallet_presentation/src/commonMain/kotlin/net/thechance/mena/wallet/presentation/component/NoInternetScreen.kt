package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_no_internet
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun NoInternetScreen(
    onRetry : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_no_internet),
            title = stringResource(Res.string.no_internet_title),
            description = stringResource(Res.string.no_internet_content),
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .heightIn(min = 48.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.retry),
            onClick = { onRetry() },
            contentPadding = PaddingValues(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._16
            )
        )
    }
}

@Preview
@Composable
private fun NoInternetScreenPreview(){
    MenaTheme {
        NoInternetScreen(onRetry = {})
    }
}