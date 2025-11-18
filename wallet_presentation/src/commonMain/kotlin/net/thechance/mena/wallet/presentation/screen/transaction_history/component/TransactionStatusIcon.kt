package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_send
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionStatusIcon(
    transactionType: TransactionHistoryScreenState.TransactionTypeUiState,
    transactionStatus: TransactionHistoryScreenState.TransactionStatusUiState,
) {
    Box(
        modifier = Modifier.height(64.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.primary.onPrimary, CircleShape)
                .padding(12.dp)
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(transactionType.iconRes),
                contentDescription = stringResource(transactionStatus.iconContentDescriptionRes),
                tint = transactionType.iconTint(),
                modifier = Modifier.size(24.dp)
            )
        }

        if (transactionStatus.contentRes == Res.string.failed) {
            Icon(
                painter = painterResource(Res.drawable.ic_failed),
                contentDescription = stringResource(Res.string.failed),
                modifier = Modifier.size(20.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun TransactionStatusIconSuccessPreview() {
    MenaTheme {
        TransactionStatusIcon(
            transactionType = TransactionHistoryScreenState.TransactionTypeUiState.SENT,
            transactionStatus = TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS
        )
    }
}

