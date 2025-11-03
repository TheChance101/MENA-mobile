package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(
                    topStart = Theme.radius.lg,
                    topEnd = Theme.radius.lg
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(0.3f)
        )

        SortableHeaderCell(
            text = stringResource(Res.string.user_name),
            sortType = UsersManagementScreenState.SortType.USERNAME,
            currentSort = sortState,
            onSortClicked = onSortClicked,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = stringResource(Res.string.phone_number),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(1.5f)
        )

        SortableHeaderCell(
            text = stringResource(Res.string.last_login_date),
            sortType = UsersManagementScreenState.SortType.LAST_LOGIN_DATE,
            currentSort = sortState,
            onSortClicked = onSortClicked,
            modifier = Modifier.weight(1.5f)
        )

        SortableHeaderCell(
            text = stringResource(Res.string.last_visit_date),
            sortType = UsersManagementScreenState.SortType.LAST_VISIT_DATE,
            currentSort = sortState,
            onSortClicked = onSortClicked,
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = stringResource(Res.string.status),
            style = Theme.typography.label.large,
            softWrap = false,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "",
            style = Theme.typography.label.large,
            modifier = Modifier.weight(0.8f)
        )
    }
}