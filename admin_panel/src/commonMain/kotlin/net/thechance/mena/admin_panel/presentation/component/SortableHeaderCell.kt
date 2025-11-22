package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.utils.noRippleClickable
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_sort
import net.thechance.mena.admin_panel.resources.sort
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> SortableHeaderCell(
    text: String,
    sortType: T,
    currentSortType: T,
    onSortClicked: (T) -> Unit,
    modifier: Modifier = Modifier,
    isSortingDisabled: Boolean = false,
) {
    val isSortActive = currentSortType == sortType

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
        AnimatedVisibility(!isSortingDisabled){
            Icon(
                painter = painterResource(Res.drawable.ic_sort),
                contentDescription = stringResource(Res.string.sort),
                modifier = Modifier.size(20.dp),
                tint = animatedIconTint
            )
        }
    }
}