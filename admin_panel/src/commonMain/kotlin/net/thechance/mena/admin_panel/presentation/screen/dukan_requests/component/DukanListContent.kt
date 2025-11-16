@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component

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
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.component.DukanImage
import net.thechance.mena.admin_panel.presentation.component.DukanLocation
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableCellText
import net.thechance.mena.admin_panel.presentation.component.ViewDukanDetailsButton
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreenState
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DukanListContent(
    state: DukanRequestsScreenState,
    listener: DukanRequestsInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        DukanTableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked
        )
        if (state.isLoading) {
            AdminPanelContentLoading()
        } else {
            DukanListTable(
                dukan = state.dukans,
                onViewDetailsClicked = listener::onViewDetailsClicked,
                modifier = Modifier.weight(1f),
            )

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
private fun DukanListTable(
    dukan: List<DukanRequestsScreenState.DukanItem>,
    onViewDetailsClicked: (dukanId: Uuid) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(items = dukan) { index, dukanItem ->
            val isLastItem = index == dukan.lastIndex
            DukanItemRow(
                index = dukanItem.index,
                dukan = dukanItem,
                isLastItem = isLastItem,
                hasBackground = index % 2 != 0,
                onViewDetailsClicked = {
                    onViewDetailsClicked(dukanItem.id)
                }
            )
        }
    }
}

@Composable
private fun DukanItemRow(
    index: Int,
    isLastItem: Boolean,
    dukan: DukanRequestsScreenState.DukanItem,
    onViewDetailsClicked: () -> Unit,
    hasBackground: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (hasBackground) Theme.colorScheme.background.surfaceLow
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

        Box(
            modifier = Modifier.weight(0.5f),
            contentAlignment = Alignment.CenterStart
        ) {
            DukanImage(imageUrl = dukan.imageUrl)
        }

        TableCellText(text = dukan.name, modifier = Modifier.weight(1f))

        DukanLocation(
            location = dukan.address,
            modifier = Modifier.weight(1.5f)
        )

        TableCellText(text = dukan.date, modifier = Modifier.weight(0.5f))

        ViewDukanDetailsButton(
            onClick = onViewDetailsClicked,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(0.8f)
        )
    }
}