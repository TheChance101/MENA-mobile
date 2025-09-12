package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class DukanStyle {
    VERTICAL,
    HORIZONTAL
}

@Composable
fun DukanStyle(
    modifier: Modifier = Modifier,
    orientation: DukanStyle = DukanStyle.HORIZONTAL,
    hasImage: Boolean = true,
    isSelected: Boolean = true,
    contentPadding: PaddingValues= PaddingValues(0.dp),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.height(198.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow).then(
                if (isSelected) modifier.border(
                    1.dp,
                    Theme.colorScheme.primary.primary,
                    RoundedCornerShape(Theme.radius.md)
                )else modifier
            ).padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (orientation == DukanStyle.HORIZONTAL) {
            if (hasImage) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(Theme.radius.md))
                        .background(Theme.colorScheme.background.surfaceLow)
                ) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(Theme.radius.sm))
                            .background(Theme.colorScheme.background.surface),
                        contentAlignment = Alignment.Center
                    ) {
//            MenaIcon()
                    }
                }
            } else {
                ShimmerRectangle(
                    modifier = Modifier.fillMaxWidth().padding(end = 11.dp),
                    backgroundColor = Theme.colorScheme.background.surface,
                    height = 7.dp,
                    cornerRadius = Theme.radius.xxs
                )
                ShimmerRectangle(
                    modifier = Modifier.fillMaxWidth().padding(end = 35.dp),
                    backgroundColor = Theme.colorScheme.background.surface,
                    height = 5.dp,
                    cornerRadius = Theme.radius.xxs
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
            }
            HorizontalStyle(Modifier.weight(1f))
            HorizontalStyle(Modifier.weight(1f))
            HorizontalStyle(Modifier.weight(1f))
        } else {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.background.surface).padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
//            MenaIcon()
            }
            Row {
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
                ShimmerRectangle(
                    Modifier.weight(1f),
                    height = 10.dp,
                    cornerRadius = Theme.radius.xxs,
                    backgroundColor = Theme.colorScheme.background.surfaceHigh
                )
            }
            VerticalStyle(Modifier.weight(1f))
            VerticalStyle(Modifier.weight(1f))
            VerticalStyle(Modifier.weight(1f))
        }
    }
}

@Composable
fun ShimmerRectangle(
    modifier: Modifier = Modifier,
    cornerRadius: Dp,
    height: Dp,
    backgroundColor: Color
) {
    Box(
        modifier = modifier.height(height).clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    )
}

@Preview
@Composable
private fun DukanStylePreview() {
    MenaTheme {
        DukanStyle()
    }
}
