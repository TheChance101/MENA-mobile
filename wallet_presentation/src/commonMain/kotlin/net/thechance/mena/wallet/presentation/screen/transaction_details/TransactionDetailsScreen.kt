package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.suwasto.capturablecompose.CaptureController
import io.github.suwasto.capturablecompose.rememberCaptureController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.share_transaction_details_error_msg
import mena.wallet_presentation.generated.resources.transaction_details_header
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.ErrorView
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.DetailsContent
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.TransactionDetailsScreenShot
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ImageSharer
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TransactionDetailsScreen(
    id: String,
    onNavigateBackClicked: () -> Unit,
    viewModel: TransactionDetailsViewModel = koinViewModel(
        key = id,
        parameters = { parametersOf(id) }
    ),
    imageSharer: ImageSharer = koinInject(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val captureController = rememberCaptureController()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionDetailsEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                shareImage = imageSharer::shareImage,
                captureImage = captureController::capture,
                showSnackBar = viewModel::showSnackBar,
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
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        tint = Theme.colorScheme.primary.primary,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackButtonClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = { interactionListener.onRefresh() }
    ) {
        Crossfade(
            targetState = state,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                state.errorState != null -> ErrorView(onRetry = { interactionListener.onRefresh() })

                else -> {
                    TransactionDetailsSuccessContent(
                        state = state,
                        interactionListener = interactionListener,
                        captureController = captureController
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailsSuccessContent(
    state: TransactionDetailsScreenState,
    interactionListener: TransactionDetailsInteractionListener,
    captureController: CaptureController
){
    Box {
        DetailsContent(
            transactionDetailsUiState = state.transactionDetailsUiState,
            onShareReceiptButtonClicked = interactionListener::onShareReceiptButtonClicked,
            isShareReceiptBtnLoading = state.isShareReceiptBtnLoading,
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
    onNavigateBackClicked: () -> Unit,
    shareImage: suspend (ByteArray, String, String) -> Unit,
    captureImage: suspend () -> Unit,
    showSnackBar: suspend (StringResource, StringResource, Boolean) -> Unit,
    onCaptureError: () -> Unit
) {
    when (effect) {
        TransactionDetailsEffect.NavigateBack -> onNavigateBackClicked()

        is TransactionDetailsEffect.ShareImage -> {
            shareImage(effect.imageBytes, effect.fileName, effect.mimeType)
        }

        TransactionDetailsEffect.CaptureImage -> {
            try {
                captureImage()
            }catch (e: Throwable){
                onCaptureError()
            }
        }

        TransactionDetailsEffect.showErrorSnackBar -> {
            showSnackBar(Res.string.error,Res.string.share_transaction_details_error_msg,false)
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
                override fun onCaptureError() {}
            },
            captureController = rememberCaptureController()
        )
    }
}