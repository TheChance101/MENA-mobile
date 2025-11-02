package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_share_
import mena.wallet_presentation.generated.resources.share_button
import mena.wallet_presentation.generated.resources.share_receipt
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionStatusUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DetailsContent(
    transactionDetailsUiState: TransactionDetailsUiState,
    onShareReceiptButtonClicked: () -> Unit,
    isShareReceiptButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DetailsSection(transactionDetailsUiState = transactionDetailsUiState)
        }
        if (transactionDetailsUiState.transactionStatus == TransactionStatusUiState.SUCCESS) {
            ShareReceiptButton(
                onShareReceiptButtonClicked = onShareReceiptButtonClicked,
                isLoading = isShareReceiptButtonLoading,
            )
        }
    }
}

@Composable
private fun ShareReceiptButton(
    onShareReceiptButtonClicked: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        text = stringResource(Res.string.share_receipt),
        onClick = onShareReceiptButtonClicked,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .heightIn(min = 48.dp)
            .fillMaxWidth(),
        trailingIcon = painterResource(Res.drawable.ic_share_),
        iconSize = 20.dp,
        contentDescription = stringResource(Res.string.share_button),
        iconStartPadding = 8.dp,
        isLoading = isLoading,
        contentColor = Theme.colorScheme.primary.primary,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(Theme.radius.md)
    )
}

@Preview
@Composable
private fun DetailsSectionPreview() {
    MenaTheme {
        DetailsContent(
            transactionDetailsUiState = TransactionDetailsUiState(),
            onShareReceiptButtonClicked = {},
            isShareReceiptButtonLoading = false,
        )
    }
}