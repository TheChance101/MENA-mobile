package net.thechance.mena.wallet.presentation.screen.export.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
            .padding(16.dp)
    )
    {
        ExportTransactionFilterSection(
            modifier = Modifier.weight(1f),
            state = state,
            interactionListener = interactionListener
        )
        OutlinedButton(
            text = stringResource(Res.string.view_and_share),
            onClick = interactionListener::onViewAndShareClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp),
            trailingIcon = painterResource(Res.drawable.share),
            isLoading = state.isViewAndShareLoading,
            isEnabled = state.isViewAndShareButtonEnabled,
            contentPadding = PaddingValues(vertical = 13.dp),
        )
        PrimaryButton(
            text = stringResource(Res.string.download),
            onClick = interactionListener::onDownloadClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            trailingIcon = painterResource(Res.drawable.download),
            isLoading = state.isDownloadLoading,
            isEnabled = state.isDownloadButtonEnabled,
            contentPadding = PaddingValues(vertical = 13.dp)
        )
    }
}


@Composable
private fun ExportTransactionFilterSection(
    modifier: Modifier = Modifier,
    state: ExportTransactionsState,
    interactionListener: ExportTransactionsListener
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            SelectCard(
                cardText = stringResource(Res.string.all_transactions),
                onCardSelected = interactionListener::onAllTransactionsClicked,
                isSelected = (!state.isCustomFilterCardSelected),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        item {
            SelectCard(
                cardText = stringResource(Res.string.custom_filtering),
                isSelected = state.isCustomFilterCardSelected,
                onCardSelected = interactionListener::onCustomFilteringClicked,
            )
        }
        item {
            Crossfade(
                targetState = state.isCustomFilterCardSelected,
                label = stringResource(Res.string.filter_crossfade),
            ) { visible ->
                if (visible) {
                    FilterSection(
                        state = state,
                        interactionListener = interactionListener
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ExportTransactionContentBodyPreview() {
    val mockState = ExportTransactionsState(
        isCustomFilterCardSelected = true,
        selectedTransactionsTypes = setOf(FilterType.SENT),
        startDate = LocalDate(2023, 4, 4),
        endDate = LocalDate(2023, 4, 4),
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
