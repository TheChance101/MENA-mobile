@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.presentation.component.LoadingIndicator
import net.thechance.mena.admin_panel.presentation.component.PagesIndicatorRow
import net.thechance.mena.admin_panel.presentation.component.TableCellText
import net.thechance.mena.admin_panel.presentation.component.ViewDukanDetailsButton
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_dukan_location
import net.thechance.mena.admin_panel.resources.ic_dukan_placholder
import net.thechance.mena.admin_panel.resources.image
import net.thechance.mena.admin_panel.resources.location
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
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
        DukanTableHeaderRow(
            sortState = state.sort,
            onSortClicked = listener::onSortClicked
        )
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            DukanListTable(
                dukan = state.dukans,
                onViewDetailsClicked = listener::onViewDetailsClicked,
                modifier = Modifier.weight(1f),
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
private fun DukanListTable(
    dukan: List<DukanManagementScreenState.Dukan>,
    onViewDetailsClicked: (Uuid) -> Unit,
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

        TableCellText(text = dukan.index.toString(), modifier = Modifier.weight(0.069f))

        Box(
            modifier = Modifier.weight(0.099f),
            contentAlignment = Alignment.CenterStart
        ) {
            DukanImage(imageUrl = dukan.imageUrl)
        }

        TableCellText(text = dukan.name, modifier = Modifier.weight(0.15f))

        DukanLocation(
            location = dukan.location,
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.02f))
        DukanStatus(
            isActive = dukan.isActive,
            modifier = Modifier.weight(0.09f )
        )
        Spacer(modifier = Modifier.weight(0.02f))
        TableCellText(text = dukan.addedDate, modifier = Modifier.weight(0.12f))

        ViewDukanDetailsButton(
            onClick = onViewDetailsClicked,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(0.13f)
        )
    }
}

@Composable
private fun DukanLocation(
    location: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_dukan_location),
            contentDescription = stringResource(Res.string.location),
            modifier = Modifier.size(20.dp)
        )
        Text(
            modifier = Modifier.weight(1f),
            text = location,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Composable
private fun DukanImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(Res.string.image),
        error = painterResource(Res.drawable.ic_dukan_placholder),
        placeholder = painterResource(Res.drawable.ic_dukan_placholder),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 64.dp, height = 44.dp)
            .clip(RoundedCornerShape(Theme.radius.sm))
    )
}