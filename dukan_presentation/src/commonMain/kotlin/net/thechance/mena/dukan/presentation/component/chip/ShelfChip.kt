package net.thechance.mena.dukan.presentation.component.chip


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.sp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun ShelfChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(Theme.radius.full),
    selectedBackgroundColor: Color = Theme.colorScheme.brand.brand,
    selectedContentColor: Color = Theme.colorScheme.primary.onPrimary
) {
    val transition = updateTransition(isSelected, label = "Chip Transition")

    val containerColor by transition.animateColor(
        label = "Chip Container Color",
        targetValueByState = { isCurrentlySelected ->
            if (isCurrentlySelected) selectedBackgroundColor
            else Theme.colorScheme.background.surfaceLow
        }
    )

    val contentColor by transition.animateColor(
        label = "Chip Content Color",
        targetValueByState = { isCurrentlySelected ->
            if (isCurrentlySelected) selectedContentColor
            else Theme.colorScheme.shadeSecondary
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            Theme.spacing._4,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .then(
                if (isEnabled) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .background(if (isEnabled) containerColor else Theme.colorScheme.disabled)
            .padding(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._12
            )
    ) {
        Text(
            text = text,
            style = Theme.typography.label.small,
            color = if (isEnabled) contentColor else Theme.colorScheme.textDisabled,
            fontSize = 10.sp,
            letterSpacing = 0.sp,
            lineHeight = 16.sp
        )
    }
}