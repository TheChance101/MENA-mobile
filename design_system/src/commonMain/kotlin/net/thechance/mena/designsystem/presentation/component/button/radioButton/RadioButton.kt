package net.thechance.mena.designsystem.presentation.component.button.radioButton

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun RadioButton(
    isSelected: Boolean,
    onClick: (() -> Unit)?,
    label: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Theme.radius.full),
    isEnabled: Boolean = true
) {

    val animatedBorderDp by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 1.dp
    )

    val animatedSelectionBorderColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.primary.primary else Theme.colorScheme.stroke
    )

    val animatedDisabledBorderColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.disabled else Theme.colorScheme.border.disabled
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isEnabled) animatedSelectionBorderColor else animatedDisabledBorderColor
    )

    val animatedUnselectedContentColor by animateColorAsState(
        targetValue = if (isSelected || !isEnabled) Color.Unspecified else Theme.colorScheme.background.surfaceLow
    )

    val animatedUnselectedLabelColor by animateColorAsState(
        targetValue = if (isSelected)
            Theme.colorScheme.shadePrimary else Theme.colorScheme.shadeTertiary
    )

    val animatedLabelColor by animateColorAsState(
        targetValue = if (isEnabled)
            animatedUnselectedLabelColor else Theme.colorScheme.stroke
    )

    val clickableModifier = onClick?.let {
        Modifier.clickable(
            enabled = isEnabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            role = Role.RadioButton
        ) {
            onClick()
        }
    } ?: Modifier

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(animatedUnselectedContentColor, shape)
                .border(
                    width = animatedBorderDp,
                    color = animatedBorderColor,
                    shape = shape
                )
                .clip(shape)
                .then(clickableModifier),
        )

        label?.let { text ->
            Text(
                text = text,
                color = animatedLabelColor,
                style = Theme.typography.label.medium
            )
        }

    }
}

@Preview
@Composable
private fun RadioButtonPreview() {
    MenaTheme {
        var selected by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            RadioButton(
                isSelected = selected,
                label = "Label",
                isEnabled = false,
                onClick = {
                    selected = !selected
                }
            )
        }
    }
}