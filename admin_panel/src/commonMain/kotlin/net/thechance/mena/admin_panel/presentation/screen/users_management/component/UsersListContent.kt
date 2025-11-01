package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.User
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun UsersListContent(
    users: List<UsersManagementScreenState.UserItem>,
    userNameSort: UsersManagementScreenState.Sort,
    lastLoginDateSort: UsersManagementScreenState.Sort,
    lastVisitDateSort: UsersManagementScreenState.Sort,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier.padding(16.dp)) {
        TableHeaderRow(
            userNameSort = userNameSort,
            lastLoginDateSort = lastLoginDateSort,
            lastVisitDateSort = lastVisitDateSort,
            listener = listener
        )
        LazyColumn(state = listState) {
            itemsIndexed(users) { index, user ->
                UserItemRow(
                    index = index + 1,
                    user = user,
                    hasBackground = index % 2 != 0,
                    onStatusClick = { listener.onStatusClicked(user.id) }
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

    val backgroundColor by animateColorAsState(
        targetValue = if (hasBackground) {
            Theme.colorScheme.background.surfaceLow
        } else {
            Theme.colorScheme.background.surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(backgroundColor)
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
            text = user.fullName,
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
        UserStatesButton(
            isActive = user.userStates == User.UserStates.ACTIVE,
            modifier = Modifier.weight(0.8f)
        )

        UserStatesToggleButton(
            isActive = user.userStates == User.UserStates.ACTIVE,
            onClick = onStatusClick,
            modifier = Modifier.weight(0.8f)
        )
    }
}