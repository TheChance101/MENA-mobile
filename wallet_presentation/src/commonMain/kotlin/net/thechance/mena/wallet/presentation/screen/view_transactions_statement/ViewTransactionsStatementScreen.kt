package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share_
import mena.wallet_presentation.generated.resources.img_no_internet
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.share_button_title
import mena.wallet_presentation.generated.resources.view_transactions
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.component.ErrorView
import net.thechance.mena.wallet.presentation.component.PdfViewer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.PdfHandler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ViewTransactionStatementScreen(
    onNavigateBackClicked: () -> Unit,
    filterParams: TransactionFilterParams? = null,
    viewModel: ViewTransactionStatementViewModel = koinViewModel(),
    pdfHandler: PdfHandler = koinInject()
) {

    LaunchedEffect(filterParams) {
        viewModel.getStatementPdf(filterParams)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            handleEffects(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                shareStatement = pdfHandler::sharePdf
            )
        }
    )

    ViewTransactionsStatementContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun ViewTransactionsStatementContent(
    state: ViewTransactionStatementScreenState,
    listener: ViewTransactionStatementInteractionListener
) {
    WalletScaffold(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface)
            .statusBarsPadding(),
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
                onLeadingClick = listener::onNavigateBackClicked,
            )
        },
        bottomContent = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
                    .height(48.dp),
                text = stringResource(Res.string.share_button_title),
                onClick = listener::onShareClicked,
                trailingIcon = painterResource(Res.drawable.ic_share_),
                iconSize = 20.dp,
                isLoading = state.statement is UiState.Loading,
            )
        },
    ) {
        StatementViewer(statement = state.statement) {
            listener.onRetryClicked()
        }
    }
}

@Composable
fun StatementViewer(
    statement: UiState<ByteArray>,
    onRetry: () -> Unit
) {
    when (statement) {
        is UiState.Error -> {
            if (statement.error is ErrorState.NoInternet)
                ErrorView(
                    image = painterResource(Res.drawable.img_no_internet),
                    title = stringResource(Res.string.no_internet_title),
                    description = stringResource(Res.string.no_internet_content),
                    onRetry = onRetry
                )
            else
                ErrorView(onRetry = onRetry)
        }

        UiState.Loading ->
            Box(modifier = Modifier.fillMaxSize()) {
                ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }

        is UiState.Success<ByteArray> -> PdfViewer(pdf = statement.data)
        UiState.Idle -> Unit
    }
}

private suspend fun handleEffects(
    effect: ViewTransactionStatementEffect,
    onNavigateBackClicked: () -> Unit,
    shareStatement: suspend (statement: ByteArray, fileName: String) -> Unit
) {
    when (effect) {
        ViewTransactionStatementEffect.NavigateBack -> onNavigateBackClicked()
        is ViewTransactionStatementEffect.ShareStatement -> {
            shareStatement(effect.statement, "statement.pdf")
        }
    }
}