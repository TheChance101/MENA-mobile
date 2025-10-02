package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share_
import mena.wallet_presentation.generated.resources.loading_pdf_message
import mena.wallet_presentation.generated.resources.share_button_title
import mena.wallet_presentation.generated.resources.view_transactions
import mena.wallet_presentation.generated.resources.view_transactions_statement_error_message
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.component.PdfViewer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.PdfHandler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ViewTransactionStatementScreen(
    onNavigateBackClicked: () -> Unit,
    viewModel: ViewTransactionStatementViewModel = koinViewModel(),
    pdfHandler: PdfHandler = koinInject()
) {
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
        }
    ) {
        StatementViewer(statement = state.statement)
    }
}

@Composable
fun StatementViewer(
    statement: UiState<ByteArray>,
) {
    when (statement) {
        is UiState.Error -> {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = stringResource(Res.string.view_transactions_statement_error_message),
                style = Theme.typography.body.medium,
                textAlign = TextAlign.Center,
            )
        }

        UiState.Loading, UiState.Idle -> {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = stringResource(Res.string.loading_pdf_message),
                style = Theme.typography.body.medium,
                textAlign = TextAlign.Center,
            )
        }

        is UiState.Success<ByteArray> -> {
            PdfViewer(pdf = statement.data)
        }
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