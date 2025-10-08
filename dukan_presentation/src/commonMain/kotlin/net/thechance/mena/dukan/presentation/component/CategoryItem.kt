package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.menu_circle
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryItem(
    categoryName: String,
    categoryImage: Any,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    ) {
        AsyncImage(
            model = categoryImage,
            contentDescription = categoryName,
            modifier = Modifier
                .size(size = 60.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = RoundedCornerShape(size = Theme.radius.full)
                )
        )
        Text(
            text = categoryName,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._4)
        )
    }
}

@Composable
fun CategoryItem(
    categoryName: String,
    categoryImage: Painter,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    ) {
        Image(
            painter = categoryImage,
            contentDescription = categoryName,
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = RoundedCornerShape(size = Theme.radius.full)
                ),
            alignment = Alignment.Center

        )
        Text(
            text = categoryName,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
        )
    }
}

@Preview
@Composable
private fun CategoryItemPreview() {
    MenaTheme {
        CategoryItem(
            categoryName = "Clothes",
            categoryImage = painterResource(Res.drawable.menu_circle),
            onClick = {}
        )
    }
}