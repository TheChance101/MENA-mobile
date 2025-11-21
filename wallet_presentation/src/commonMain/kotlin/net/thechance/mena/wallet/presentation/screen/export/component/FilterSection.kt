package net.thechance.mena.wallet.presentation.screen.export.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.FilterContent
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionsListener
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionsState
import net.thechance.mena.wallet.presentation.utils.formatLocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterSection(
    state: ExportTransactionsState.FilterState,
    interactionListener: ExportTransactionsListener
) {
    Column {
        HorizontalDivider()
        FilterContent(
            showStatusFilter = false,
            selectedTypes = state.selectedTransactionsTypes,
            startDate = state.startDate?.let { formatLocalDate(date = it, outputFormat = "yyyy/MM/dd") } ?: "",
            endDate = state.endDate?.let { formatLocalDate(date = it, outputFormat = "yyyy/MM/dd") } ?: "",
            onTypeSelected = interactionListener::onTypeSelected,
            onStartDateClicked = interactionListener::onStartDateClicked,
            onEndDateClicked = interactionListener::onEndDateClicked
        )
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(
                color = Theme.colorScheme.stroke,
                shape = CircleShape
            )
    )
}


@Preview
@Composable
private fun FilterSectionPreview() {
    val mockState = ExportTransactionsState.FilterState(
        selectedTransactionsTypes = setOf(FilterType.SENT, FilterType.ONLINE_PURCHASE),
        startDate = LocalDate(2023, 4, 2),
        endDate = LocalDate(2024, 4, 2)
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
            FilterSection(
                state = mockState,
                interactionListener = mockListener
            )
        }

    }
}
