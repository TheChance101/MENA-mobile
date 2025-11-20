package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.suwasto.capturablecompose.CaptureController
import io.github.suwasto.capturablecompose.rememberCaptureController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.share_image
import mena.wallet_presentation.generated.resources.transaction_details_header
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.DetailsContent
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.TransactionDetailsScreenShot
import net.thechance.mena.wallet.presentation.utils.FileSharer
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TransactionDetailsScreen(
    viewModel: TransactionDetailsViewModel = koinViewModel(),
    fileSharer: FileSharer = koinInject(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val captureController = rememberCaptureController()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionDetailsEffect(
                effect = effect,
                navController = navController,
                shareImage = fileSharer::shareFile,
                captureImage = captureController::capture,
                onCaptureError = viewModel::onCaptureError
            )
        }
    )

    TransactionDetailsScreenContent(
        state = state,
        interactionListener = viewModel,
        captureController = captureController
    )
}

@Composable
private fun TransactionDetailsScreenContent(
    state: TransactionDetailsScreenState,
    interactionListener: TransactionDetailsInteractionListener,
    captureController: CaptureController
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.transaction_details_header),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = { BackIcon() },
                onLeadingClick = interactionListener::onBackButtonClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        isLoading = state.isLoading,
        onRetry = { interactionListener.onRefresh() }
    ) {

        TransactionDetailsSuccessContent(
            state = state,
            interactionListener = interactionListener,
            captureController = captureController
        )
    }
}

@Composable
private fun TransactionDetailsSuccessContent(
    state: TransactionDetailsScreenState,
    interactionListener: TransactionDetailsInteractionListener,
    captureController: CaptureController
) {
    Box {
        DetailsContent(
            transactionDetailsUiState = state.transactionDetailsUiState,
            onShareReceiptButtonClicked = interactionListener::onShareReceiptButtonClicked,
            isShareReceiptButtonLoading = state.isShareReceiptBtnLoading,
        )
        TransactionDetailsScreenShot(
            captureController = captureController,
            onScreenShotCapture = { imageBitmap ->
                interactionListener.onScreenShotCaptured(
                    byteArray = imageBitmapToByteArray(imageBitmap),
                    fileName = state.transactionDetailsUiState.id
                )
            },
            transactionDetailsUiState = state.transactionDetailsUiState,
        )
    }
}

private suspend fun onTransactionDetailsEffect(
    effect: TransactionDetailsEffect,
    navController: NavController,
    shareImage: suspend (
        image: ByteArray,
        fileName: String,
        mimeType: String,
        shareTitle: String
    ) -> Unit,
    captureImage: suspend () -> Unit,
    onCaptureError: suspend () -> Unit
) {
    when (effect) {
        TransactionDetailsEffect.NavigateBack -> navController.popBackStack()

        is TransactionDetailsEffect.ShareImage -> {
            shareImage(
                effect.imageBytes,
                effect.fileName,
                effect.mimeType,
                getString(Res.string.share_image)
            )
        }

        TransactionDetailsEffect.CaptureImage -> {
            try {
                captureImage()
            } catch (_: Throwable) {
                onCaptureError()
            }
        }
    }
}

@Preview
@Composable
private fun TransactionDetailsScreenPreview() {
    MenaTheme {
        TransactionDetailsScreenContent(
            state = TransactionDetailsScreenState(TransactionDetailsUiState()),
            interactionListener = object : TransactionDetailsInteractionListener {
                override fun onBackButtonClicked() {}
                override fun onShareReceiptButtonClicked() {}
                override fun onScreenShotCaptured(byteArray: ByteArray, fileName: String) {}
                override fun onRefresh() {}
                override suspend fun onCaptureError() {}
            },
            captureController = rememberCaptureController()
        )
    }
}