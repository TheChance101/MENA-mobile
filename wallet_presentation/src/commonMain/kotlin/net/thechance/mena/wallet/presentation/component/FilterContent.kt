package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.ic_calendar
import mena.wallet_presentation.generated.resources.select_date
import mena.wallet_presentation.generated.resources.status
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterContent(
    showStatusFilter: Boolean = true,
    selectedTypes: Set<FilterType>? = null,
    selectedStatus: FilterStatus = FilterStatus.ALL,
    fromDate: String,
    toDate: String,
    onTypeSelected: (FilterType) -> Unit = {},
    onStatusSelected: (FilterStatus) -> Unit = {},
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = stringResource(Res.string.type),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadePrimary
        )

        TransactionTypesRow(
            selectedTypes = selectedTypes,
            onTypeSelected = onTypeSelected
        )
        if (showStatusFilter) {
            Text(
                text = stringResource(Res.string.status),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary
            )

            TransactionStatusRow(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected
            )
        }

        DateRangePicker(
            fromDate = fromDate,
            toDate = toDate,
            onFromClick = onFromClick,
            onToClick = onToClick
        )
    }
}

@Composable
private fun TransactionTypesRow(
    selectedTypes: Set<FilterType>? = null,
    onTypeSelected: (FilterType) -> Unit = {}
) {
    LazyRow(
        horizontalArrangement = Arrangement
            .spacedBy(8.dp),
        modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
    ) {
        items(FilterType.entries) { type ->
            Chip(
                text = stringResource(type.labelRes),
                isSelected = selectedTypes?.contains(type) == true,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun TransactionStatusRow(
    selectedStatus: FilterStatus = FilterStatus.ALL,
    onStatusSelected: (FilterStatus) -> Unit = {}
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
    ) {
        items(FilterStatus.entries) { status ->
            Chip(
                text = stringResource(status.labelRes),
                isSelected = selectedStatus == status,
                onClick = {
                    onStatusSelected(status)
                }
            )
        }
    }
}

@Composable
fun DateRangePicker(
    fromDate: String,
    toDate: String,
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        DatePickerField(
            label = stringResource(Res.string.from),
            value = fromDate,
            onClick = onFromClick,
            modifier = Modifier.weight(1f)
        )

        DatePickerField(
            label = stringResource(Res.string.to),
            value = toDate,
            onClick = onToClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DatePickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadePrimary
        )

        TextField(
            value = value,
            hint = stringResource(Res.string.select_date),
            onValueChanged = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(shape = RoundedCornerShape(Theme.radius.md))
                .clickable { onClick() },
            trailingIcon = painterResource(Res.drawable.ic_calendar)
        )
    }
}


@Preview
@Composable
private fun FilterContentPreview() {
    MenaTheme {
        Column(
            Modifier.background(Theme.colorScheme.background.surface)
        ) {
            FilterContent(
                selectedTypes = setOf(FilterType.SENT, FilterType.ONLINE_PURCHASE),
                selectedStatus = FilterStatus.ALL,
                fromDate = "2025/09/01",
                toDate = "2025/09/30",
                onFromClick = { },
                onToClick = { }
            )
        }
    }
}