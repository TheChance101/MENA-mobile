package net.thechance.mena.wallet.presentation.screen.export

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.export_transactions
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.component.CustomToast
import net.thechance.mena.wallet.presentation.screen.export.component.ExportTransactionContentBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExportTransactionScreen(
    onNavigateBackClicked: () -> Unit,
    navigateToVewTransactionStatement: () -> Unit,
    viewModel: ExportTransactionsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onExportTransactionsEffect(
                effect,
                onNavigateBackClicked,
                navigateToVewTransactionStatement
            )
        }
    )

    ExportTransactionScreenContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun ExportTransactionScreenContent(
    state: ExportTransactionsState,
    interactionListener: ExportTransactionsListener
) {

    WalletScaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.export_transactions),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick =  interactionListener::onBackClicked ,
            )
        },
        snackBar = {
            SnackBarContainer(snackBarState = state.snackBar)
        },
        toast = {
            CustomToast(
                toastState = state.toast,
            )
        }
    ) {
        ExportTransactionContentBody(
            state = state,
            interactionListener = interactionListener
        )
    }
}

private fun onExportTransactionsEffect(
    effect: ExportTransactionsEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToVewTransactionStatement: () -> Unit,
) {
    when (effect) {
        is ExportTransactionsEffect.NavigateBack -> onNavigateBackClicked()

        is ExportTransactionsEffect.NavigateToViewFileScreen
            -> navigateToVewTransactionStatement()
    }
}

@Composable
@Preview
private fun ExportTransactionScreenPreview() {
    MenaTheme {
        ExportTransactionScreenContent(
            state = ExportTransactionsState(
                isCustomFilterCardSelected = true
            ),
            interactionListener = object : ExportTransactionsListener {
                override fun onBackClicked() {}
                override fun onAllTransactionsClicked() {}
                override fun onCustomFilteringClicked() {}
                override fun onTypeSelected(type: FilterType) {}
                override fun onFromDateClicked() {}
                override fun onToDateClicked() {}
                override fun onViewAndShareClicked() {}
                override fun onDownloadClicked() {}
            }
        )
    }
}