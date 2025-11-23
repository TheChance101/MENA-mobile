package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.SortableHeaderCell
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.last_login_date
import net.thechance.mena.admin_panel.resources.last_visit_date
import net.thechance.mena.admin_panel.resources.phone_number
import net.thechance.mena.admin_panel.resources.status
import net.thechance.mena.admin_panel.resources.user_name
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun TableHeaderRow(
    sortState: UsersManagementScreenState.SortState,
    onSortClicked: (UsersManagementScreenState.SortType) -> Unit,
    horizontalScrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
    isSortingDisabled: Boolean = false,
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
        SortableHeaderCell(
            text = stringResource(Res.string.user_name),
            sortType = UsersManagementScreenState.SortType.USERNAME,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.width(268.dp),
            isSortingDisabled = isSortingDisabled
        )
        Text(
            text = stringResource(Res.string.phone_number),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.widthIn(min = 171.dp)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.last_login_date),
            sortType = UsersManagementScreenState.SortType.LAST_LOGIN_DATE,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 175.dp),
            isSortingDisabled = isSortingDisabled
        )
        SortableHeaderCell(
            text = stringResource(Res.string.last_visit_date),
            sortType = UsersManagementScreenState.SortType.LAST_VISIT_DATE,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 167.dp),
            isSortingDisabled = isSortingDisabled
        )
        SortableHeaderCell(
            text = stringResource(Res.string.status),
            sortType = UsersManagementScreenState.SortType.ACTIVATION_STATUS,
            currentSortType = sortState.type,
            onSortClicked = onSortClicked,
            modifier = Modifier.widthIn(min = 126.dp),
            isSortingDisabled = isSortingDisabled
        )
        Text(
            text = "",
            style = Theme.typography.label.large,
            modifier = Modifier.widthIn(min = 151.dp)
        )
    }
}