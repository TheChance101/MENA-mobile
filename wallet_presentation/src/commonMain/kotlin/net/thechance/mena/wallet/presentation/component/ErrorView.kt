package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_no_internet
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.retry
import mena.wallet_presentation.generated.resources.unknown_error_description
import mena.wallet_presentation.generated.resources.unknown_error_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ErrorView(
    image: Painter,
    title: String,
    description: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding( 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatePlaceholder(
            image = image,
            title = title,
            description = description,
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .heightIn(min = 48.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.retry),
            onClick = { onRetry() },
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        )
    }
}

@Preview
@Composable
private fun NoInternetScreenPreview() {
    MenaTheme {
        ErrorView(
            image = painterResource(Res.drawable.img_no_internet),
            title = stringResource(Res.string.no_internet_title),
            description = stringResource(Res.string.no_internet_content),
            onRetry = {}
        )
    }
}