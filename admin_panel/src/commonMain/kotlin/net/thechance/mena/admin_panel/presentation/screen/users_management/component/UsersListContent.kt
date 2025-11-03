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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableHeaderRow
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

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        TableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked
        )
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            itemsIndexed(state.users) { index, user ->
                UserItemRow(
                    index = index + 1,
                    user = user,
                    hasBackground = index % 2 != 0,
                    onToggleUserStatusClicked = {
                        listener.onToggleUserStatusClicked(
                            userId = user.id,
                            userStatus = user.status
                        )
                    }
                )
            }
        }
        PagesIndicatorRow(
            currentPage = state.pageInfo.page,
            totalPages = state.pageInfo.totalPages,
            onPageChanged = listener::onPageChanged,
            modifier = Modifier.padding(top = 8.dp, bottom = 14.dp).align(Alignment.Start)
        )
    }
}

@Composable
private fun UserItemRow(
    index: Int,
    user: UsersManagementScreenState.UserItem,
    hasBackground: Boolean,
    onToggleUserStatusClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (hasBackground) Theme.colorScheme.background.surfaceLow else Theme.colorScheme.background.surface

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(animatedBackgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(0.3f)
        )

        Text(
            text = user.fullName,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = user.phoneNumber,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = user.lastLoginAt,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = user.lastVisitAt,
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadePrimary,
            softWrap = false,
            modifier = Modifier.weight(1.5f)
        )

        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            UserStatusButton(isActive = user.status == Status.ACTIVE)
        }

        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            UserStatusToggleButton(
                isActive = user.status == Status.ACTIVE,
                onClick = onToggleUserStatusClicked
            )
        }
    }
}