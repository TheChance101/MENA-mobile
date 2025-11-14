package net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreenState
import net.thechance.mena.admin_panel.presentation.utils.noRippleClickable
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.date
import net.thechance.mena.admin_panel.resources.dukan_name
import net.thechance.mena.admin_panel.resources.ic_sort
import net.thechance.mena.admin_panel.resources.image
import net.thechance.mena.admin_panel.resources.location
import net.thechance.mena.admin_panel.resources.sort
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanTableHeaderRow(
    sortState: DukanRequestsScreenState.SortState,
    onSortClicked: (DukanRequestsScreenState.SortType) -> Unit,
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
        Text(
            text = stringResource(Res.string.image),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier.weight(0.5f)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.dukan_name),
            sortType = DukanRequestsScreenState.SortType.DUKAN_NAME,
            currentSort = sortState,
            onSortClicked = onSortClicked,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.location),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.weight(1.5f)
        )
        SortableHeaderCell(
            text = stringResource(Res.string.date),
            sortType = DukanRequestsScreenState.SortType.DATE,
            currentSort = sortState,
            onSortClicked = onSortClicked,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = "",
            style = Theme.typography.label.large,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun SortableHeaderCell(
    text: String,
    sortType: DukanRequestsScreenState.SortType,
    currentSort: DukanRequestsScreenState.SortState,
    onSortClicked: (DukanRequestsScreenState.SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSortActive = currentSort.type == sortType

    val iconTint = if (isSortActive) Theme.colorScheme.success else Theme.colorScheme.shadePrimary

    val animatedIconTint by animateColorAsState(
        targetValue = iconTint,
        animationSpec = tween(durationMillis = 300),
        label = "iconTint"
    )

    Row(
        modifier = modifier.noRippleClickable(onClick = { onSortClicked(sortType) }),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            painter = painterResource(Res.drawable.ic_sort),
            contentDescription = stringResource(Res.string.sort),
            modifier = Modifier.size(20.dp),
            tint = animatedIconTint
        )
    }
}
