package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CategoryItem(
    category: Selectable<CategoryUiState>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (category.isSelected) Theme.colorScheme.shadePrimary else Theme.colorScheme.shadeSecondary
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = Theme.radius.full))
            .background(color = Theme.colorScheme.primary.onPrimary)
            .noRippleClickable { category.value.id?.let { onClick(it) } }
            .padding(all = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = category.value.emoji,
                style = Theme.typography.label.extraSmall,
                fontSize = 14.sp
            )
            Text(
                text = category.value.name,
                color = textColor,
                style = Theme.typography.label.extraSmall
            )
        }
        RadioButton(
            isSelected = category.isSelected,
            onClick = { category.value.id?.let { onClick(it) } },
        )
    }
}

@Preview
@Composable
private fun CategoryItemPreview() {
    var isSelected by remember { mutableStateOf(false) }
    val category = CategoryUiState(
        id = "1",
        name = "Hello",
        emoji = "\uD83D\uDE00"
    )
    val selectableCategory = Selectable(category, isSelected)
    MenaTheme {
        CategoryItem(
            category = selectableCategory,
            onClick = {
                isSelected = isSelected.not()
            }
        )
    }
}