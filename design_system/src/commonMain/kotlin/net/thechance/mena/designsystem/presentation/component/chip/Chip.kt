package net.thechance.mena.designsystem.presentation.component.chip

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Chip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    painter: Painter? = null,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(Theme.radius.full)
) {
    val transition = updateTransition(isSelected)
    val containerColor by transition.animateColor(
        targetValueByState = { isCurrentSelected ->
            if (isCurrentSelected) Theme.colorScheme.brand.brand
            else Theme.colorScheme.background.surfaceLow
        }
    )
    val contentColor by transition.animateColor(
        targetValueByState = { isCurrentSelected ->
            if (isCurrentSelected) Theme.colorScheme.primary.onPrimary
            else Theme.colorScheme.shadeSecondary
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
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

        painter?.let { iconPainter ->
            Icon(
                painter = iconPainter,
                modifier = Modifier.size(iconSize),
                contentDescription = null,
                tint = if (isEnabled) contentColor else Theme.colorScheme.textDisabled
            )
        }

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

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)

@Composable
private fun ChipPreview() {
    MenaTheme {
        PreviewComponent(
            title = "Chips"
        ) {
            Chip(
                text = "Chips",
                painter = painterResource(Res.drawable.ic_chip),
                isSelected = false,
                onClick = {}
            )

            Chip(
                text = "Chips",
                painter = painterResource(Res.drawable.ic_chip),
                isSelected = true,
                onClick = {}
            )

            Chip(
                text = "Chips",
                painter = painterResource(Res.drawable.ic_chip),
                isSelected = true,
                isEnabled = false,
                onClick = {}
            )
        }
    }
}