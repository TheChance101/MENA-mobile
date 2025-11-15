package net.thechance.mena.designsystem.presentation.component.checkBox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.checkmark
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Checkbox(
    checkedState: ToggleableState,
    onCheckedChange: ((ToggleableState) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(Theme.radius.xs),
    intermediateLineShape: Shape = RoundedCornerShape(Theme.radius.xxs),
    contentPadding: PaddingValues = PaddingValues(3.dp)
) {

    val animatedContainerColor by animateColorAsState(
        targetValue = if (isEnabled)
            Theme.colorScheme.primary.primary else Theme.colorScheme.disabled
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isEnabled)
            Theme.colorScheme.primary.onPrimary else Theme.colorScheme.textDisabled
    )

    val animatedUncheckedBorderColor by animateColorAsState(
        targetValue = if (checkedState == ToggleableState.Off)
            Theme.colorScheme.border.disabled else Color.Unspecified
    )


    val animatedUncheckedContainerColor by animateColorAsState(
        targetValue = if (checkedState == ToggleableState.Off)
            Theme.colorScheme.background.surfaceLow else animatedContainerColor
    )

    val animatedUncheckedBorderDp by animateDpAsState(
        targetValue = if (checkedState == ToggleableState.Off)
            1.dp else 0.dp
    )


    val animatedUncheckedLabelColor by animateColorAsState(
        targetValue = if (checkedState == ToggleableState.Off)
            Theme.colorScheme.shadeTertiary else Theme.colorScheme.shadePrimary
    )

    val animatedLabelColor by animateColorAsState(
        targetValue = if (isEnabled)
            animatedUncheckedLabelColor else Theme.colorScheme.stroke
    )

    val clickableModifier = onCheckedChange?.let {
        Modifier.clickable(
            enabled = isEnabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            role = Role.Checkbox
        ) {
            onCheckedChange(checkedState)
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
                .background(
                    color = animatedUncheckedContainerColor,
                    shape = shape
                )
                .border(animatedUncheckedBorderDp, animatedUncheckedBorderColor, shape)
                .clip(shape)
                .then(clickableModifier)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            when (checkedState) {
                ToggleableState.On -> Icon(
                    modifier = Modifier,
                    tint = animatedContentColor,
                    painter = painterResource(Res.drawable.checkmark),
                    contentDescription = "Checkmark icon"
                )

                ToggleableState.Indeterminate -> HorizontalDivider(
                    thickness = 2.dp,
                    color = animatedContentColor,
                    modifier = Modifier
                        .size(10.dp, 2.dp)
                        .clip(intermediateLineShape)
                )

                ToggleableState.Off -> {}
            }
        }

        label?.let { text ->
            Text(
                text = text,
                color = animatedLabelColor,
                style = Theme.typography.label.medium
            )
        }

    }
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
private fun CheckboxPreview() {
    MenaTheme {
        PreviewComponent(
            title = "check box"
        ) {
            Checkbox(
                checkedState = ToggleableState.Off,
                onCheckedChange = {}
            )

            Checkbox(
                checkedState = ToggleableState.Off,
                isEnabled = false,
                onCheckedChange = {}
            )

            Checkbox(
                checkedState = ToggleableState.On,
                onCheckedChange = {}
            )

            Checkbox(
                checkedState = ToggleableState.On,
                isEnabled = false,
                onCheckedChange = {}
            )

            Checkbox(
                checkedState = ToggleableState.Indeterminate,
                onCheckedChange = {}
            )

            Checkbox(
                checkedState = ToggleableState.Indeterminate,
                isEnabled = false,
                onCheckedChange = {}
            )
        }
    }
}
