package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share_
import mena.wallet_presentation.generated.resources.share_button_title
import mena.wallet_presentation.generated.resources.view_transactions
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.UiState
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

    Content(
        state = state,
        listener = viewModel
    )
}

private suspend fun handleEffects(
    effect: ViewTransactionStatementEffect,
    onNavigateBackClicked: () -> Unit,
    shareStatement: suspend (statement: ByteArray, fileName: String) -> Unit
) {
    when (effect) {
        ViewTransactionStatementEffect.NavigatedBack -> onNavigateBackClicked()
        is ViewTransactionStatementEffect.ShareStatement -> {
            shareStatement(effect.statement, "statement.pdf")
        }
    }
}

@Composable
private fun Content(
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
                text = "an error happened while loading the statement file",
                style = Theme.typography.body.medium,
                textAlign = TextAlign.Center,
            )
        }

        UiState.Loading, UiState.Idle -> {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = "Loading PDF...",
                style = Theme.typography.body.medium,
                textAlign = TextAlign.Center,
            )
        }

        is UiState.Success<ByteArray> -> {
            PdfViewer(pdf = statement.data)
        }
    }
}

@Composable
fun PdfViewer(
    pdf: ByteArray,
    pdfHandler: PdfHandler = koinInject()
) {
    var pages by remember { mutableStateOf(emptyList<ByteArray>()) }
    var finishedSplitingThePdfToPages by remember { mutableStateOf(false) }
    LaunchedEffect(pdf) {
        pages = pdfHandler.splitToPagesOfPngs(pdfData = pdf)
        finishedSplitingThePdfToPages = true
    }

    if (finishedSplitingThePdfToPages.not()) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = "Rendering PDF...",
            style = Theme.typography.body.medium,
            textAlign = TextAlign.Center,
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            itemsIndexed(pages) { index, page ->
                AsyncImage(
                    model = page,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7071f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                )
            }
        }
    }
}