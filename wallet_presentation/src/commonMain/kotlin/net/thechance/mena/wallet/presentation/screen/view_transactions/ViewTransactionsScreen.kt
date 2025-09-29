package net.thechance.mena.wallet.presentation.screen.view_transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.view_transactions
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.BitmapImage
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ViewTransactionStatementScreen(
    onNavigateBackClicked: () -> Unit,
    viewModel: ViewTransactionsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getBitmap()
    }

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onViewTransactionsEffect(
                effect,
                onNavigateBackClicked
            )
        }
    )

    ViewTransactionsContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
fun ViewTransactionsContent(
    state: ViewTransactionsScreenState,
    interactionListener: ViewTransactionsInteractionListener
) {
    println("hiiiiiiiiiiiiii")
    WalletScaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.view_transactions),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.share),
                    trailingIcon = painterResource(Res.drawable.ic_share),
                    onClick = {
                        val pdfBytes = state.pdfBytes ?: byteArrayOf()
                        val fileName = state.fileName
                        interactionListener.onShareClicked(pdfBytes, fileName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) }
    ) {
        println("i waana kill me self")
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            state.bitmap?.let { bitmap ->
                BitmapImage(
                    bitmap = bitmap,
                    contentDescription = stringResource(Res.string.view_transactions),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
private fun onViewTransactionsEffect(
    effect: ViewTransactionsEffect,
    onNavigateBackClicked: () -> Unit
) {
    when (effect) {
        ViewTransactionsEffect.NavigateBack -> onNavigateBackClicked()

    }
}

@Preview
@Composable
private fun PreviewViewTransactionsWithPdfScreen() {
    MenaTheme {
        ViewTransactionsContent(
            state = ViewTransactionsScreenState(),
            interactionListener = object : ViewTransactionsInteractionListener {
                override fun onBackClicked() {}
                override fun onShareClicked(pdfBytes: ByteArray, fileName: String) {}
            }
        )
    }
}