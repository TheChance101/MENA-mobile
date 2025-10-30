package net.thechance.mena.wallet.presentation.screen.export

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.export_transactions
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.pick_end_date
import mena.wallet_presentation.generated.resources.pick_start_date
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.DatePickerBottomSheet
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.StatementDetailsScreenRoute
import net.thechance.mena.wallet.presentation.screen.export.component.DownloadingToast
import net.thechance.mena.wallet.presentation.screen.export.component.ExportTransactionContentBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import net.thechance.mena.wallet.presentation.utils.orToday
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExportTransactionScreen(viewModel: ExportTransactionsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onExportTransactionsEffect(effect = effect, navController = navController)
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
                onLeadingClick = interactionListener::onBackClicked,
            )
        },
        snackBar = {
            SnackBarContainer(snackBarState = state.snackBar)
        },
        overlays = {
            bottomSheet(isVisible = state.dateState.isDateBottomSheetVisible) { isVisible ->
                DatePickerBottomSheet(
                    isVisible = isVisible,
                    defaultSelectedDate = when (state.dateState.datePickerMode) {
                        ExportTransactionsState.DatePickerMode.START_DATE ->
                            state.dateState.defaultStartDate.orToday()

                        ExportTransactionsState.DatePickerMode.END_DATE ->
                            state.dateState.defaultEndDate.orToday()
                    },
                    title = when (state.dateState.datePickerMode) {
                        ExportTransactionsState.DatePickerMode.START_DATE ->
                            stringResource(Res.string.pick_start_date)

                        ExportTransactionsState.DatePickerMode.END_DATE ->
                            stringResource(Res.string.pick_end_date)
                    },
                    onPickClick = { day, month, year ->
                        val pickedDate = LocalDate(year, month, day)
                        interactionListener.onPickDateClicked(pickedDate)
                    },
                    onDismiss = interactionListener::onDismissDatePicker
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ExportTransactionContentBody(
                state = state,
                interactionListener = interactionListener
            )

            DownloadingToast(
                toastState = state.toast
            )
        }
    }
}

private fun onExportTransactionsEffect(
    effect: ExportTransactionsEffect,
    navController: NavController
) {
    when (effect) {
        is ExportTransactionsEffect.NavigateBack -> {
            navController.popBackStack()
        }

        is ExportTransactionsEffect.NavigateToViewFileScreen -> {
            navController.navigate(
                StatementDetailsScreenRoute(
                    effect.statementLocation
                )
            )
        }
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
                override fun onStartDateClicked() {}
                override fun onEndDateClicked() {}
                override fun onDismissDatePicker() {}
                override fun onPickDateClicked(date: LocalDate) {}
                override fun onViewAndShareClicked() {}
                override fun onDownloadClicked() {}
            }
        )
    }
}