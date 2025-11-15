package net.thechance.mena.designsystem.presentation.component.switches

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Switch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onContainerColor: Color = Theme.colorScheme.brand.brand,
    onContentColor: Color = Theme.colorScheme.primary.onPrimary,
    onDisabledContainerColor: Color = Theme.colorScheme.brand.brand,
    onDisabledContentColor: Color = Theme.colorScheme.primary.onPrimary,
    offContainerColor: Color = Theme.colorScheme.background.surfaceHigh,
    offContentColor: Color = Theme.colorScheme.primary.onPrimaryBody,
    offDisabledContainerColor: Color = Theme.colorScheme.disabled,
    offDisabledContentColor: Color = Theme.colorScheme.primary.onPrimaryBody,
) {
    val transition = updateTransition(isChecked)
    val containerColor by transition.animateColor(
        targetValueByState = { checked ->
            if (checked) onContainerColor else offContainerColor
        }
    )
    val contentColor by transition.animateColor(
        targetValueByState = { checked ->
            if (checked) onContentColor else offContentColor
        }
    )
    val disabledContainerColor =
        if (isChecked) onDisabledContainerColor else offDisabledContainerColor
    val disabledContentColor = if (isChecked) onDisabledContentColor else offDisabledContentColor
    val contentAlignment = transition.animateFloat {
        if (isChecked) 1f else -1f
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(if (isEnabled) containerColor else disabledContainerColor)
            .width(48.dp)
            .then(
                if (!isEnabled) Modifier.alpha(0.5f)
                else Modifier.clickable {
                    onCheckedChange(!isChecked)
                }
            )
    ) {
        Box(
            modifier = Modifier
                .align(BiasAlignment(contentAlignment.value, 0f))
                .padding(4.dp)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(if (isEnabled) contentColor else disabledContentColor)
                .size(20.dp)

        )
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
private fun PreviewSwitch() {
    MenaTheme {
        val (isSwitchChecked, onCheckedChange) = remember { mutableStateOf(false) }

        PreviewComponent(
            title = "Switch"
        ) {
            Switch(
                isChecked = isSwitchChecked,
                onCheckedChange = onCheckedChange
            )

            Switch(
                isChecked = false,
                isEnabled = false,
                onCheckedChange = onCheckedChange
            )

            Switch(
                isChecked = true,
                onCheckedChange = onCheckedChange
            )

            Switch(
                isChecked = true,
                isEnabled = false,
                onCheckedChange = onCheckedChange
            )
        }
    }
}