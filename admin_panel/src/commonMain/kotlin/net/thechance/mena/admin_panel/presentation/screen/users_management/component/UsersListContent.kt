@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.presentation.component.LoadingIndicator
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableCellText
import net.thechance.mena.admin_panel.presentation.component.TableHeaderRow
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun UsersListContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        TableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked
        )

        if (state.isLoading) {
            LoadingIndicator()
        } else {
            UsersListTable(
                users = state.users,
                onToggleUserStatusClicked = listener::onToggleUserStatusClicked,
                modifier = Modifier.weight(1f),
                pageInfo = state.pageInfo
            )
        }

        PagesIndicatorRow(
            currentPage = state.pageInfo.page,
            totalPages = state.pageInfo.totalPages,
            onPageChanged = listener::onPageChanged,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 14.dp)
                .align(Alignment.Start)
        )
    }
}

@Composable
private fun UsersListTable(
    users: List<UsersManagementScreenState.UserItem>,
    pageInfo: UsersManagementScreenState.UserPageInfo,
    onToggleUserStatusClicked: (userId: Uuid, userStatus: User.Status) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(users) { index, user ->
            val isLastItem = index == users.lastIndex
            UserItemRow(
                index = user.index,
                user = user,
                isLastItem = isLastItem,
                hasBackground = index % 2 != 0,
                onToggleUserStatusClicked = {
                    onToggleUserStatusClicked(user.id, user.status)
                }
            )
        }
    }
}

@Composable
private fun UserItemRow(
    index: Int,
    isLastItem: Boolean,
    user: UsersManagementScreenState.UserItem,
    hasBackground: Boolean,
    onToggleUserStatusClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (hasBackground) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.background.surface

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                animatedBackgroundColor,
                shape = if (isLastItem) RoundedCornerShape(
                    bottomStart = Theme.radius.lg,
                    bottomEnd = Theme.radius.lg
                ) else RectangleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TableCellText(text = index.toString(), modifier = Modifier.weight(0.3f))

        TableCellText(text = user.fullName, modifier = Modifier.weight(2f))

        TableCellText(text = user.phoneNumber, modifier = Modifier.weight(1.5f))

        TableCellText(text = user.lastLoginAt, modifier = Modifier.weight(1.5f))

        TableCellText(text = user.lastVisitAt, modifier = Modifier.weight(1.5f))

        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            UserStatusButton(isActive = user.status == User.Status.ACTIVE)
        }

        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            UserStatusToggleButton(
                isActive = user.status == User.Status.ACTIVE,
                onClick = onToggleUserStatusClicked
            )
        }
    }
}