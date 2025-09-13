package net.thechance.mena.dukan.presentation.screen.createDukan.content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_image
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: () -> Unit = {}
) {

    val baseModifier = modifier
        .height(198.dp)
        .clip(RoundedCornerShape(Theme.radius.md))
        .background(Theme.colorScheme.background.surfaceLow)
        .clickable { onClick() }
        .then(
            if (isSelected) Modifier.border(
                1.dp,
                Theme.colorScheme.primary.primary,
                RoundedCornerShape(Theme.radius.md)
            )
            else Modifier
        )
        .padding(contentPadding)

    Column(
        modifier = baseModifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        when (orientation) {
            DukanStyle.HORIZONTAL -> HorizontalContent(hasImage, Modifier.weight(1f))
            DukanStyle.VERTICAL -> VerticalContent(
                Modifier.weight(1f),
                Modifier.align(Alignment.Start)
            )
        }
    }
}

@Composable
private fun HorizontalContent(hasImage: Boolean, modifier: Modifier = Modifier) {
    if (hasImage) {
        ImageBox()
    } else {
        TextPlaceholders()
    }

    PlaceholderRow()
    repeat(if (hasImage) 3 else 4) {
        if (it == 4) {
            HorizontalStyle()
        } else {
            HorizontalStyle(modifier)
        }
    }
}

@Composable
private fun VerticalContent(modifier: Modifier = Modifier, align: Modifier) {
    SmallImageIcon(align)
    PlaceholderRow()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(4) {
            VerticalStyle(modifier)
        }
    }
}

@Composable
private fun PlaceholderRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) {
            ShimmerRectangle(
                Modifier.weight(1f),
                height = 10.dp,
                cornerRadius = Theme.radius.xxs,
                backgroundColor = Theme.colorScheme.background.surfaceHigh
            )
        }
    }
}

@Composable
private fun ImageBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_image),
                tint = Theme.colorScheme.primary.onPrimary,
                contentDescription = "style has image",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SmallImageIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surface)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        MenaIcon(
            painter = painterResource(Res.drawable.ic_image),
            tint = Theme.colorScheme.primary.onPrimary,
            contentDescription = "style has image",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TextPlaceholders() {
    ShimmerRectangle(
        modifier = Modifier.fillMaxWidth().padding(end = 11.dp),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 7.dp,
        cornerRadius = Theme.radius.xxs
    )
    ShimmerRectangle(
        modifier = Modifier.fillMaxWidth().padding(end = 35.dp, bottom = 11.dp),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 5.dp,
        cornerRadius = Theme.radius.xxs
    )
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
