@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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
import net.thechance.mena.admin_panel.presentation.component.ActivationStatusButton
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.component.DukanImage
import net.thechance.mena.admin_panel.presentation.component.DukanLocation
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableCellText
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.deactivated
import net.thechance.mena.admin_panel.resources.ic_view_details
import net.thechance.mena.admin_panel.resources.view_details
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DukanManagementTableContent(
    state: DukanManagementScreenState,
    listener: DukanManagementInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        val horizontalScrollState = rememberScrollState()

        DukanTableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked,
            horizontalScrollState = horizontalScrollState,
            modifier = Modifier.fillMaxWidth(),
            isSortingDisabled = state.isSortingDisabled
        )
        if (state.isLoading) {
            AdminPanelContentLoading()
        } else {
            DukanListTable(
                dukan = state.dukans,
                onViewDetailsClicked = listener::onViewDetailsClicked,
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
private fun DukanListTable(
    dukan: List<DukanManagementScreenState.Dukan>,
    onViewDetailsClicked: (Uuid) -> Unit,
    horizontalScrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        itemsIndexed(items = dukan) { index, dukanItem ->
            val isLastItem = index == dukan.lastIndex
            DukanItemRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState),
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
    isLastItem: Boolean,
    dukan: DukanManagementScreenState.Dukan,
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
        TableCellText(text = dukan.index.toString(), modifier = Modifier.widthIn(min = 78.dp))

        Box(
            modifier = Modifier.widthIn(min = 112.dp),
            contentAlignment = Alignment.CenterStart
        ) { DukanImage(imageUrl = dukan.imageUrl) }

        TableCellText(text = dukan.name, modifier = Modifier.width(200.dp))

        DukanLocation(
            location = dukan.location,
            iconSize = 20.dp,
            iconTint = Theme.colorScheme.shadePrimary,
            textColor = Theme.colorScheme.shadePrimary,
            textStyle = Theme.typography.label.large,
            spacing = 8.dp,
            modifier = Modifier.widthIn(min = 244.dp, max = 244.dp)
        )

        Box(
            modifier = Modifier.widthIn(min = 157.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            ActivationStatusButton(
                isActive = dukan.isActive,
                deactivationText = stringResource(resource = Res.string.deactivated)
            )
        }

        TableCellText(text = dukan.addedDate, modifier = Modifier.widthIn(min = 168.dp))

        Box(
            modifier = Modifier
                .widthIn(min = 177.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            OutlinedButton(
                text = stringResource(Res.string.view_details),
                trailingIcon = painterResource(Res.drawable.ic_view_details),
                onClick = onViewDetailsClicked,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
                modifier = Modifier.wrapContentWidth(),
            )
        }
    }
}