package net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.presentation.component.SortableHeaderCell
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.added_date
import net.thechance.mena.admin_panel.resources.dukan_name
import net.thechance.mena.admin_panel.resources.image
import net.thechance.mena.admin_panel.resources.location
import net.thechance.mena.admin_panel.resources.status
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanTableHeaderRow(
    sortState: DukanManagementScreenState.SortState,
    onSortClicked: (DukansSortType) -> Unit,
    horizontalScrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(
                    topStart = Theme.radius.lg,
                    topEnd = Theme.radius.lg
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.widthIn(min = 78.dp)
        )
        Text(
            text = stringResource(Res.string.image),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier.widthIn(min = 112.dp)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.dukan_name),
            sortType = DukansSortType.NAME,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 200.dp)
        )
        Text(
            text = stringResource(Res.string.location),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.widthIn(min = 244.dp)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.status),
            sortType = DukansSortType.ACTIVATION_STATUS,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 157.dp)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.added_date),
            sortType = DukansSortType.CREATED_AT,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 168.dp)
        )
        Text(
            text = "",
            style = Theme.typography.label.large,
            modifier = Modifier.widthIn(min = 177.dp)
        )
    }
}