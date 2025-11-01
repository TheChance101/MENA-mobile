package net.thechance.mena.admin_panel.presentation.screen.users_management.component

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
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_arrow_left
import net.thechance.mena.admin_panel.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaginationRow(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageNavigationButton(
            icon = Res.drawable.ic_arrow_left,
            isEnabled = currentPage > 1,
            onClick = { onPageChange(currentPage - 1) },
            modifier = Modifier.padding(end = 8.dp)
        )

        repeat(totalPages) { index ->
            val pageNumber = index + 1
            PageNumberButton(
                pageNumber = pageNumber,
                isSelected = pageNumber == currentPage,
                onClick = { onPageChange(pageNumber) },
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        PageNavigationButton(
            icon = Res.drawable.ic_arrow_right,
            isEnabled = currentPage < totalPages,
            onClick = { onPageChange(currentPage + 1) },
            modifier = Modifier.padding(start = 8.dp)
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
    val backgroundColor by animateColorAsState(
        targetValue = if (isEnabled) {
            Theme.colorScheme.background.surfaceLow
        } else {
            Theme.colorScheme.background.surfaceLow.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackgroundColor"
    )

    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(backgroundColor)
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
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colorScheme.primary.primary
        } else {
            Theme.colorScheme.background.surfaceLow
        },
        animationSpec = tween(durationMillis = 300),
        label = "pageBackgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colorScheme.primary.onPrimary
        } else {
            Theme.colorScheme.shadeSecondary
        },
        animationSpec = tween(durationMillis = 300),
        label = "pageTextColor"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$pageNumber",
            style = Theme.typography.label.large,
            color = textColor
        )
    }
}