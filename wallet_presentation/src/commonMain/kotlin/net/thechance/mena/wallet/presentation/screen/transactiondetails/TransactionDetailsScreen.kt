package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.share_button
import mena.wallet_presentation.generated.resources.share_receipt
import mena.wallet_presentation.generated.resources.transaction_details_header
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transactiondetails.component.DetailsSection
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionDetailsScreen() {
    TransactionDetailsScreenContent()
}

@Composable
private fun TransactionDetailsScreenContent(){
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.transaction_details_header),
                titleColor = Theme.colorScheme.shadePrimary,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                contentTitlePadding = PaddingValues(start = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        tint = Theme.colorScheme.primary.primary,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = {  },
            )
        }
    ){
        Box(modifier = Modifier.fillMaxSize()) {
            DetailsSection(modifier = Modifier.align(Alignment.Center))
            OutlinedButton(
                text = stringResource(Res.string.share_receipt),
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .heightIn(min = 48.dp)
                    .fillMaxWidth(),
                trailingIcon = painterResource(Res.drawable.ic_share),
                iconSize = 20.dp,
                contentDescription = stringResource(Res.string.share_button),
                iconStartPadding = 8.dp,
                isLoading = false,
                contentColor = Theme.colorScheme.primary.primary,
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(Theme.radius.md)
            )
        }
    }
}

@Preview
@Composable
private fun TransactionDetailsScreenPreview(){
    MenaTheme {
        TransactionDetailsScreenContent()
    }
}