package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.TextWithIcon
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_sort
import net.thechance.mena.admin_panel.resources.last_login_date
import net.thechance.mena.admin_panel.resources.last_visit_date
import net.thechance.mena.admin_panel.resources.phone_number
import net.thechance.mena.admin_panel.resources.status
import net.thechance.mena.admin_panel.resources.user_name
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun TableHeaderRow(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(
                    topStart = Theme.radius.md,
                    topEnd = Theme.radius.md
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableHeaderRowContent(
            state = state,
            listener = listener
        )
    }
}

@Composable
private fun TableHeaderRowContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.primary.primary,
            modifier = Modifier.weight(0.3f)
        )

        TextWithIcon(
            text = stringResource(Res.string.user_name),
            icon = painterResource(Res.drawable.ic_sort),
            modifier = Modifier.weight(2f),
            isSelected = state.sortUserName.ascending || state.sortUserName.descending,
            onClick = { listener.onSortUsersNameClicked() }
        )

        Text(
            text = stringResource(Res.string.phone_number),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.primary.primary,
            modifier = Modifier.weight(1.5f)
        )

        TextWithIcon(
            text = stringResource(Res.string.last_login_date),
            icon = painterResource(Res.drawable.ic_sort),
            modifier = Modifier.weight(1.5f),
            isSelected = state.sortLastLoginDate.ascending || state.sortLastLoginDate.descending,
            onClick = { listener.onSortLastLoginDateClicked() }
        )

        TextWithIcon(
            text = stringResource(Res.string.last_visit_date),
            icon = painterResource(Res.drawable.ic_sort),
            modifier = Modifier.weight(1.5f),
            isSelected = state.sortLastVisitDate.ascending || state.sortLastVisitDate.descending,
            onClick = { listener.onSortLastVisitDateClicked() }
        )

        Text(
            text = stringResource(Res.string.status),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.primary.primary,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "",
            style = Theme.typography.label.large,
            modifier = Modifier.weight(0.8f)
        )
    }
}