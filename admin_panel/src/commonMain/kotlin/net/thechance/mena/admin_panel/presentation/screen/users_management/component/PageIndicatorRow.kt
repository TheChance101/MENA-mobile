package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_arrow_left
import net.thechance.mena.admin_panel.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
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
        PaginationButton(
            icon = Res.drawable.ic_arrow_left,
            enabled = currentPage > 1,
            onClick = { onPageChange(currentPage - 1) },
            modifier = Modifier.padding(end = 8.dp)
        )

        repeat(totalPages) { index ->
            val pageNumber = index + 1
            PageNumberItem(
                pageNumber = pageNumber,
                isSelected = pageNumber == currentPage,
                onClick = { onPageChange(pageNumber) }
            )
            if (pageNumber < totalPages) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        PaginationButton(
            icon = Res.drawable.ic_arrow_right,
            enabled = currentPage < totalPages,
            onClick = { onPageChange(currentPage + 1) }
        )
    }
}

@Composable
private fun PaginationButton(
    icon: org.jetbrains.compose.resources.DrawableResource,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                if (enabled) Theme.colorScheme.background.surfaceLow
                else Theme.colorScheme.background.surfaceLow.copy(alpha = 0.5f)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (enabled) Theme.colorScheme.primary.primary
            else Theme.colorScheme.primary.primary.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun PageNumberItem(
    pageNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(
                if (isSelected) Theme.colorScheme.primary.primary
                else Theme.colorScheme.background.surfaceLow
            )
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$pageNumber",
            style = Theme.typography.body.medium,
            color = if (isSelected) Theme.colorScheme.primary.onPrimary
            else Theme.colorScheme.primary.primary
        )
    }
}