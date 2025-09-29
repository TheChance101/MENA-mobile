package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.Capturable
import io.github.suwasto.capturablecompose.CaptureController
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun TransactionDetailsScreenShot(
    captureController: CaptureController,
    onScreenShotCapture: (ImageBitmap) -> Unit,
    transactionDetailsUiState: TransactionDetailsUiState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.alpha(0f)) {
        Capturable(
            captureController = captureController,
            onCaptured = { onScreenShotCapture(it) }
        ) {
            DetailsSection(
                modifier = modifier,
                transactionDetailsUiState = transactionDetailsUiState,
                isUserNameShown = true
            )
        }
    }
}

@Preview
@Composable
private fun TransactionDetailsScreenShotPreview() {
    MenaTheme {
        DetailsSection(
            transactionDetailsUiState = TransactionDetailsUiState(),
            isUserNameShown = true
        )
    }
}