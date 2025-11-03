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
import androidx.compose.ui.unit.dp
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
        PageNavigationButton(
            icon = Res.drawable.ic_arrow_left,
            isEnabled = currentPage > 0,
            onClick = { onPageChanged(currentPage - 1) },
            modifier = Modifier.padding(end = 8.dp)
        )

        displayedPages.forEach { page ->
            if (page == null) {
                PageEllipsisButton(modifier = Modifier.padding(start = 6.dp))
            } else {
                PageNumberButton(
                    pageNumber = page + 1,
                    isSelected = page == currentPage,
                    onClick = { onPageChanged(page) },
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        PageNavigationButton(
            icon = Res.drawable.ic_arrow_right,
            isEnabled = currentPage < totalPages - 1,
            onClick = { onPageChanged(currentPage + 1) },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PageEllipsisButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "…",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
private fun PageNavigationButton(
    icon: DrawableResource,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isEnabled) {
        Theme.colorScheme.background.surfaceLow
    } else {
        Theme.colorScheme.background.surfaceLow.copy(alpha = 0.5f)
    }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
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
        tint = Theme.colorScheme.primary.primary
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
        if (isSelected) Theme.colorScheme.primary.primary else Theme.colorScheme.background.surfaceLow
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

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animatedBackgroundColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pageNumber.toString(),
            style = Theme.typography.label.large,
            color = animatedTextColor
        )
    }
}