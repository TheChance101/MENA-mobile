package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.close
import mena.wallet_presentation.generated.resources.connection_lost_try_again
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.payment_failed_description
import mena.wallet_presentation.generated.resources.show_transaction_details
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.transaction_success
import mena.wallet_presentation.generated.resources.transaction_successful
import mena.wallet_presentation.generated.resources.try_again
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PaymentStatusContent(
    image: Painter = painterResource(Res.drawable.transaction_failed),
    title: String = stringResource(Res.string.transaction_failed),
    description: String = stringResource(Res.string.connection_lost_try_again),
    primaryButtonText: String = stringResource(Res.string.try_again),
    onBackClicked: () -> Unit = {},
    onPrimaryButtonClick: () -> Unit = {},
    onOutlinedButtonClicked: () -> Unit = {},
    hasAppBar: Boolean = true
) {

    WalletScaffold(
        topBar = {
            if (hasAppBar) {
                AppBar(
                    title = "",
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back_button)
                        )
                    },
                    onLeadingClick = onBackClicked
                )
            }
        }
    ) {
        PaymentStatusBody(
            image = image,
            title = title,
            description = description,
            primaryButtonText = primaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            onOutlinedButtonClicked = onOutlinedButtonClicked
        )
    }
}

@Composable
private fun PaymentStatusBody(
    image: Painter,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onOutlinedButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PaymentResultCard(
            image = image,
            title = title,
            description = description,
            modifier = Modifier.align(Alignment.Center)
        )

        PaymentStatusButtons(
            primaryButtonText = primaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            onOutlinedButtonClicked = onOutlinedButtonClicked,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PaymentStatusButtons(
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onOutlinedButtonClicked: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        PrimaryButton(
            text = primaryButtonText,
            onClick = onPrimaryButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(vertical = 13.dp)
        )

        OutlinedButton(
            text = stringResource(Res.string.close),
            onClick = onOutlinedButtonClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp),
            contentPadding = PaddingValues(vertical = 13.dp),
        )
    }
}

@Composable
private fun PaymentResultCard(
    image: Painter,
    title: String,
    description: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 150.dp)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = image,
            contentDescription = title
        )
        Text(
            text = title,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
        )
        Text(
            text = description,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.extraSmall,
            modifier = Modifier
                .padding(top = 8.dp)
        )
    }
}

@Composable
@Preview
private fun PaymentConfirmationStatusLostConnectionScreenPreview() {
    MenaTheme {
        PaymentStatusContent()
    }
}

@Composable
@Preview
private fun PaymentConfirmationStatusUnknownErrorScreenPreview() {
    MenaTheme {
        PaymentStatusContent(
            description = stringResource(Res.string.payment_failed_description)
        )
    }
}

@Composable
@Preview
private fun PaymentConfirmationStatusSuccessfulScreenPreview() {
    MenaTheme {
        PaymentStatusContent(
            hasAppBar = false,
            image = painterResource(Res.drawable.transaction_success),
            primaryButtonText = stringResource(Res.string.show_transaction_details),
            title = stringResource(Res.string.transaction_successful),
            description = "You paid 31.99 silvers to Ahmed Ali"
        )
    }
}