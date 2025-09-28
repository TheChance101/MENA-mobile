package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.suwasto.capturablecompose.rememberCaptureController
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
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.DetailsSection
import net.thechance.mena.wallet.presentation.screen.transaction_details.component.TransactionDetailsScreenShot
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionDetailsEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked
            )
        }
    )

    TransactionDetailsScreenContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun TransactionDetailsScreenContent(
    state: TransactionDetailsScreenState,
    interactionListener: TransactionDetailsInteractionListener
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
    ) {
        Crossfade(
            targetState = state.transactionDetailsUiState,
            modifier = Modifier.fillMaxSize()
        ) { transactionState ->
            when (transactionState) {
                is UiState.Error -> {}
                is UiState.Loading, UiState.Idle -> {}
                is UiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val captureController = rememberCaptureController()
                        DetailsSection(
                            modifier = Modifier.padding(bottom = 88.dp).align(Alignment.Center),
                            transactionDetailsUiState = transactionState.data
                        )
                        if (transactionState.data.transactionStatus == Transaction.Status.SUCCESS) {
                            OutlinedButton(
                                text = stringResource(Res.string.share_receipt),
                                onClick = {
                                    interactionListener.onShareReceiptButtonClicked(
                                        capture = captureController::capture
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        horizontal = Theme.spacing._16,
                                        vertical = Theme.spacing._24
                                    )
                                    .heightIn(min = 48.dp)
                                    .fillMaxWidth(),
                                trailingIcon = painterResource(Res.drawable.ic_share),
                                iconSize = 20.dp,
                                contentDescription = stringResource(Res.string.share_button),
                                iconStartPadding = Theme.spacing._8,
                                isLoading = state.isShareReceiptBtnLoading,
                                contentColor = Theme.colorScheme.primary.primary,
                                contentPadding = PaddingValues(
                                    vertical = Theme.spacing._8,
                                    horizontal = Theme.spacing._16
                                ),
                                shape = RoundedCornerShape(Theme.radius.md)
                            )
                            TransactionDetailsScreenShot(
                                captureController = captureController,
                                onScreenShotCapture = { imageBitmap ->
                                    interactionListener.onScreenShotCaptured(
                                        byteArray = imageBitmapToByteArray(imageBitmap),
                                        fileName = transactionState.data.id
                                    )
                                },
                                transactionDetailsUiState = transactionState.data,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun onTransactionDetailsEffect(
    effect: TransactionDetailsEffect,
    onNavigateBackClicked: () -> Unit,
) {
    when (effect) {
        TransactionDetailsEffect.NavigateBack -> {
            onNavigateBackClicked()
        }
    }
}

@Preview
@Composable
private fun TransactionDetailsScreenPreview() {
    MenaTheme {
        TransactionDetailsScreenContent(
            state = TransactionDetailsScreenState(
                transactionDetailsUiState = UiState.Success(
                    TransactionDetailsUiState()
                )
            ),
            interactionListener = object : TransactionDetailsInteractionListener {
                override fun onBackButtonClicked() {}
                override fun onShareReceiptButtonClicked(capture: suspend () -> Unit) {}
                override fun onScreenShotCaptured(byteArray: ByteArray, fileName: String) {}
                override fun onRefresh() {}
            }
        )
    }
}