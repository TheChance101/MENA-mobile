package net.thechance.mena.wallet.presentation.screen.statement_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_share_
import mena.wallet_presentation.generated.resources.share_button_title
import mena.wallet_presentation.generated.resources.share_pdf
import mena.wallet_presentation.generated.resources.statement
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.screen.statement_details.components.PdfViewer
import net.thechance.mena.wallet.presentation.utils.FileSharer
import net.thechance.mena.wallet.presentation.utils.MimeType
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StatementDetailsScreen(
    statementLocation: StorageLocation,
    viewModel: StatementDetailsViewModel = koinViewModel(
        parameters = { parametersOf(statementLocation) }
    ),
    fileSharer: FileSharer = koinInject()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            handleEffects(
                effect = effect,
                navController = navController,
                shareStatement = fileSharer::shareFile
            )
        }
    )

    StatementDetailsContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun StatementDetailsContent(
    state: StatementDetailsScreenState,
    listener: StatementDetailsInteractionListener
) {
    var pagesReady by remember { mutableStateOf(false) }

    WalletScaffold(
        modifier = Modifier
            .background(Theme.colorScheme.background.surface)
            .statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.statement),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = { BackIcon() },
                onLeadingClick = listener::onNavigateBackClicked,
            )
        },
        isLoading = state.isLoading,
        bottomContent = {
            if (pagesReady && state.statement.isNotEmpty()) {
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
                    isLoading = false,
                )
            }
        },
        errorState = state.errorState
    ) {
        PdfViewer(pdf = state.statement) { ready ->
            pagesReady = ready
        }
    }
}

private suspend fun handleEffects(
    effect: StatementDetailsEffect,
    navController: NavController,
    shareStatement: suspend (statement: ByteArray, fileName: String, mimeType: String, shareTitle: String) -> Unit
) {

    when (effect) {
        StatementDetailsEffect.NavigateBack -> navController.popBackStack()
        is StatementDetailsEffect.ShareStatement -> {
            shareStatement(
                effect.statement,
                STATEMENT_FILE_NAME,
                MimeType.PDF,
                getString(Res.string.share_pdf)
            )
        }
    }
}

private const val STATEMENT_FILE_NAME = "statement.pdf"