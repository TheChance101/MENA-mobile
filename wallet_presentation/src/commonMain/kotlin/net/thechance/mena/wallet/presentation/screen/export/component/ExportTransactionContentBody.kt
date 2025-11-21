package net.thechance.mena.wallet.presentation.screen.export.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.all_transactions
import mena.wallet_presentation.generated.resources.custom_filtering
import mena.wallet_presentation.generated.resources.download
import mena.wallet_presentation.generated.resources.filter_crossfade
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.view_and_share
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionsListener
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionsState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExportTransactionContentBody(
    state: ExportTransactionsState,
    interactionListener: ExportTransactionsListener
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ExportTransactionFilterSection(
            state = state,
            interactionListener = interactionListener,
            modifier = Modifier.padding(top = 16.dp)
        )

        ExportTransactionActionButtons(
            isViewAndShareLoading = state.isViewAndShareLoading,
            isViewAndShareEnabled = state.isViewAndShareButtonEnabled,
            isDownloadLoading = state.isDownloadLoading,
            isDownloadEnabled = state.isDownloadButtonEnabled,
            interactionListener = interactionListener,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}


@Composable
private fun ExportTransactionFilterSection(
    modifier: Modifier = Modifier,
    state: ExportTransactionsState,
    interactionListener: ExportTransactionsListener
) {
    Column(
        modifier = modifier
    ) {
        ExportTypeCard(
            cardTitle = stringResource(Res.string.all_transactions),
            onCardSelected = interactionListener::onAllTransactionsClicked,
            isSelected = (!state.isCustomFilterCardSelected),
            isEnabled = (!state.canSelectExportType),
            modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
        )

        ExportTypeCard(
            cardTitle = stringResource(Res.string.custom_filtering),
            isSelected = state.isCustomFilterCardSelected,
            isEnabled = (!state.canSelectExportType),
            onCardSelected = interactionListener::onCustomFilteringClicked,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        AnimatedVisibility(
            visible = state.isCustomFilterCardSelected,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)),
            label = stringResource(Res.string.filter_crossfade),
        ) {
            FilterSection(
                state = state.filterState,
                interactionListener = interactionListener
            )
        }
    }
}

@Composable
private fun ExportTransactionActionButtons(
    isViewAndShareLoading: Boolean,
    isViewAndShareEnabled: Boolean,
    isDownloadLoading: Boolean,
    isDownloadEnabled: Boolean,
    interactionListener: ExportTransactionsListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        OutlinedButton(
            text = stringResource(Res.string.view_and_share),
            onClick = interactionListener::onViewAndShareClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp),
            trailingIcon = painterResource(Res.drawable.share),
            isLoading = isViewAndShareLoading,
            isEnabled = isViewAndShareEnabled,
            contentPadding = PaddingValues(vertical = 13.dp),
        )

        PrimaryButton(
            text = stringResource(Res.string.download),
            onClick = interactionListener::onDownloadClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            trailingIcon = painterResource(Res.drawable.download),
            isLoading = isDownloadLoading,
            isEnabled = isDownloadEnabled,
            contentPadding = PaddingValues(vertical = 13.dp)
        )
    }
}

@Preview
@Composable
private fun ExportTransactionContentBodyPreview() {
    val mockState = ExportTransactionsState(
        isCustomFilterCardSelected = true,
        filterState = ExportTransactionsState.FilterState(
            selectedTransactionsTypes = setOf(FilterType.SENT),
            startDate = LocalDate(2023, 4, 4),
            endDate = LocalDate(2023, 4, 4),
        ),
        isViewAndShareLoading = false,
        isViewAndShareButtonEnabled = true,
        isDownloadLoading = false,
        isDownloadButtonEnabled = true
    )

    val mockListener = object : ExportTransactionsListener {
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

    MenaTheme {
        Column(
            Modifier
                .fillMaxHeight()
                .background(Theme.colorScheme.background.surface)
        ) {
            ExportTransactionContentBody(
                state = mockState,
                interactionListener = mockListener
            )
        }
    }
}
