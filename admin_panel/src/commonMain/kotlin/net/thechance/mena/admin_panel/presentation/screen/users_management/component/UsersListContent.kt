package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun UsersListContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier.padding(16.dp)) {
        TableHeaderRow(state, listener)
        LazyColumn(state = listState) {
            itemsIndexed(state.filteredUsers) { index, user ->
                UserItemRow(
                    index = index + 1,
                    user = user,
                    hasBackground = index % 2 != 0,
                    onStatusClick = { listener.onStatusClick(user.id) }
                )
            }
        }
    }
}

@Composable
private fun UserItemRow(
    index: Int,
    user: UsersManagementScreenState.UserItem,
    hasBackground: Boolean,
    onStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                if (hasBackground) Theme.colorScheme.background.surfaceLow
                else Theme.colorScheme.background.surface
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$index",
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = user.userName,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = user.phoneNumber,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = user.lastLoginDate.toString(),
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = user.lastVisitDate.toString(),
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1.5f)
        )
        UserStatus(
            status = user.userStates,
            modifier = Modifier.weight(0.8f)
        )

        StatusManager(
            userStates = user.userStates,
            onClick = onStatusClick,
            modifier = Modifier.weight(0.8f)
        )
    }
}