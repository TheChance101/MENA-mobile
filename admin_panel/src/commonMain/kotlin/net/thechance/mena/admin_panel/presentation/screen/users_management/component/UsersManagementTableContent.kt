@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.presentation.component.ActivationStatusButton
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableCellText
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.blocked
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun UsersManagementTableContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        val horizontalScrollState = rememberScrollState()

        TableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked,
            horizontalScrollState = horizontalScrollState,
            modifier = Modifier.fillMaxWidth(),
            isSortingDisabled = state.isSortingDisabled
        )

        if (state.isLoading) {
            AdminPanelContentLoading()
        } else {
            UsersListTable(
                users = state.users,
                onToggleUserStatusClicked = listener::onToggleUserStatusClicked,
                horizontalScrollState = horizontalScrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        if (state.pageInfo.totalPages > 1) {
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
}

@Composable
private fun UsersListTable(
    users: List<UsersManagementScreenState.UserItem>,
    onToggleUserStatusClicked: (userId: Uuid, userStatus: User.Status) -> Unit,
    horizontalScrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        itemsIndexed(users) { index, user ->
            val isLastItem = index == users.lastIndex
            UserItemRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState),
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
            .background(
                animatedBackgroundColor,
                shape = if (isLastItem) RoundedCornerShape(
                    bottomStart = Theme.radius.lg,
                    bottomEnd = Theme.radius.lg
                ) else RectangleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCellText(text = index.toString(), modifier = Modifier.widthIn(min = 78.dp))

        TableCellText(text = user.fullName, modifier = Modifier.widthIn(min = 268.dp))

        TableCellText(text = user.phoneNumber, modifier = Modifier.widthIn(min = 171.dp))

        TableCellText(text = user.lastLoginAt, modifier = Modifier.widthIn(min = 175.dp))

        TableCellText(text = user.lastVisitAt, modifier = Modifier.widthIn(min = 167.dp))

        Box(
            modifier = Modifier.widthIn(min = 126.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            ActivationStatusButton(
                isActive = user.status == User.Status.ACTIVE,
                deactivationText = stringResource(resource = Res.string.blocked)
            )
        }

        Box(
            modifier = Modifier.widthIn(min = 151.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            UserStatusToggleButton(
                isActive = user.status == User.Status.ACTIVE,
                onClick = onToggleUserStatusClicked
            )
        }
    }
}