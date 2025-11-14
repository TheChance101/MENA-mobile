package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.utils.PageItem
import net.thechance.mena.admin_panel.presentation.utils.getDisplayedPages
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_arrow_left
import net.thechance.mena.admin_panel.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PagesIndicatorRow(
    currentPage: Int,
    totalPages: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedPages = getDisplayedPages(currentPage, totalPages)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (totalPages > 1) {
            PageNavigationButton(
                icon = Res.drawable.ic_arrow_left,
                isEnabled = currentPage > 0,
                onClick = { onPageChanged(currentPage - 1) },
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        displayedPages.forEach { pageItem ->
            when (pageItem) {
                is PageItem.Page -> PageNumberButton(
                    pageNumber = pageItem.number + 1,
                    isSelected = pageItem.number == currentPage,
                    onClick = { onPageChanged(pageItem.number) },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )

                PageItem.Ellipsis -> PageEllipsisBox(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
        if (totalPages > 1) {
            PageNavigationButton(
                icon = Res.drawable.ic_arrow_right,
                isEnabled = currentPage < totalPages - 1,
                onClick = { onPageChanged(currentPage + 1) },
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun PageEllipsisBox(modifier: Modifier = Modifier) {
    Text(
        text = "…",
        style = Theme.typography.label.large,
        color = Theme.colorScheme.shadeSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(vertical = 4.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}

@Composable
private fun PageNavigationButton(
    icon: DrawableResource,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isEnabled) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.disabled

    val iconTint =
        if (isEnabled) Theme.colorScheme.primary.primary else Theme.colorScheme.textDisabled

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
    )

    val animatedIconTint by animateColorAsState(
        targetValue = iconTint,
        animationSpec = tween(durationMillis = 300),
        label = "buttonIconTint"
    )

    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animatedBackgroundColor)
            .clickable(enabled = isEnabled) { onClick() }
            .padding(12.dp),
        tint = animatedIconTint
    )
}

@Composable
private fun PageNumberButton(
    pageNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) Theme.colorScheme.primary.primary
        else Theme.colorScheme.background.surfaceLow

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "pageBackgroundColor"
    )

    val textColor =
        if (isSelected) Theme.colorScheme.primary.onPrimary else Theme.colorScheme.shadeSecondary
    val animatedTextColor by animateColorAsState(
        targetValue = textColor,
        animationSpec = tween(durationMillis = 300),
        label = "pageTextColor"
    )

    Text(
        text = pageNumber.toString(),
        style = Theme.typography.label.large,
        color = animatedTextColor,
        textAlign = TextAlign.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animatedBackgroundColor)
            .clickable (enabled = !isSelected){ onClick() }
            .padding(vertical = 4.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}