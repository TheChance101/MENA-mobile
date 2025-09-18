package net.thechance.mena.designsystem.presentation.component.segment

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun RowScope.SegmentButton(
    option: String, isSelected: Boolean, onSelectChange: () -> Unit
) {
    val animatedButtonColor = animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.background.surfaceHigh,
    )

    Box(
        modifier = Modifier
            .height(40.dp)
            .weight(1f)
            .padding(4.dp)
            .shadow(
                if (isSelected) 20.dp else 0.dp,
                clip = false,
                shape = RoundedCornerShape(Theme.radius.md),
                spotColor = Color(0x00000003)
            )
            .then(
                if (isSelected) Modifier.border(
                    if (isSelected) 0.5.dp else 0.dp,
                    shape = RoundedCornerShape(Theme.radius.md),
                    color = Theme.colorScheme.stroke
                ) else Modifier
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animatedButtonColor.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Theme.colorScheme.primary.primary)
            ) {
                onSelectChange()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = option,
            style = Theme.typography.label.medium,
            color = if (isSelected) Theme.colorScheme.shadePrimary else Theme.colorScheme.shadeSecondary,
        )
    }
}

@Preview
@Composable
fun PreviewSegmentButton() {
    Row {
        SegmentButton(
            option = "My Trends",
            isSelected = true,
            onSelectChange = { }
        )
        SegmentButton(
            option = "Favorite",
            isSelected = false,
            onSelectChange = { }
        )
    }
}