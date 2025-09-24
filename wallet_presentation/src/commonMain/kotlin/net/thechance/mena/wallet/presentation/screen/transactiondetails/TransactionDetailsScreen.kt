package net.thechance.mena.wallet.presentation.screen.transactiondetails

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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.share_button
import mena.wallet_presentation.generated.resources.share_receipt
import mena.wallet_presentation.generated.resources.transaction_details_header
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.base.UiState.Idle.isLoading
import net.thechance.mena.wallet.presentation.base.UiState.Idle.isSuccess
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transactiondetails.component.DetailsSection
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.Transaction
import net.thechance.mena.wallet.presentation.screen.transactiondetails.component.ShareTransactionDetailsBottomSheetContent
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionDetailsScreen(
    viewModel: TransactionDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = ::onTransactionDetailsEffect
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
                onLeadingClick = interactionListener::onBackBtnClicked,
            )
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        overlays = {
            bottomSheet(isVisible = state.shareReceipt.isSuccess){
                BottomSheet(
                    isVisible = state.shareReceipt.isSuccess,
                    onDismissRequest = {},
                    sheetContent = {
                        item {
                            ShareTransactionDetailsBottomSheetContent(
                                image = painterResource(Res.drawable.img_silver),
                                onSendToDeviceBtnClicked = interactionListener::onSendToDeviceBtnClicked
                            )
                        }
                    }
                )
            }
        }
    ) {
        Crossfade(
            targetState = state.transaction,
            modifier = Modifier.fillMaxSize()
        ) { transactionState ->
            when (transactionState) {
                is UiState.Error -> TODO()
                is UiState.Loading, UiState.Idle -> TODO()
                is UiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        DetailsSection(
                            modifier = Modifier.align(Alignment.Center),
                            transaction = transactionState.data
                        )
                        OutlinedButton(
                            text = stringResource(Res.string.share_receipt),
                            onClick = interactionListener::onShareReceiptBtnClicked,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .heightIn(min = 48.dp)
                                .fillMaxWidth(),
                            trailingIcon = painterResource(Res.drawable.ic_share),
                            iconSize = 20.dp,
                            contentDescription = stringResource(Res.string.share_button),
                            iconStartPadding = 8.dp,
                            isLoading = state.shareReceipt.isLoading,
                            contentColor = Theme.colorScheme.primary.primary,
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                            shape = RoundedCornerShape(Theme.radius.md)
                        )
                    }
                }
            }
        }
    }
}

private fun onTransactionDetailsEffect(effect: TransactionDetailsEffect) {
    when (effect) {
        TransactionDetailsEffect.NavigateBack -> TODO()
    }
}

@Preview
@Composable
private fun TransactionDetailsScreenPreview() {
    MenaTheme {
        TransactionDetailsScreenContent(
            state = TransactionDetailsScreenState(transaction = UiState.Success(Transaction())),
            interactionListener = object : TransactionDetailsInteractionListener {
                override fun onBackBtnClicked() {}
                override fun onShareReceiptBtnClicked() {}
                override fun onRefresh() {}
                override fun onSendToDeviceBtnClicked() {}
            }
        )
    }
}