package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.category_icon
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.skeletonLoading
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryCard(
    title: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    iconContainerSize: Dp = 60.dp
) {
    var imageState by rememberSaveable { mutableStateOf(CategoryImageState.Loading) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = Theme.spacing._4)
                .clip(shape = CircleShape)
                .background(color = Theme.colorScheme.background.surfaceLow)
                .size(size = iconContainerSize)
                .clickable(onClick = onClick)
                .skeletonLoading(isLoading = imageState == CategoryImageState.Loading),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(resource = Res.string.category_icon),
                modifier = Modifier.size(iconSize),
                colorFilter = ColorFilter.tint(color = Theme.colorScheme.primary.primary),
                error = painterResource(Res.drawable.ic_no_image_loaded),
                onSuccess = { imageState = CategoryImageState.Success },
                onError = { imageState = CategoryImageState.Error },
                onLoading = { imageState = CategoryImageState.Loading }
            )
        }

        Text(
            text = title,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            minLines = 2,
            textAlign = TextAlign.Center,
        )
    }
}

private enum class CategoryImageState { Loading, Success, Error }

@Preview
@Composable
private fun CategoryPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(color = Theme.colorScheme.background.surface)
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            CategoryCard(
                title = "Category",
                imageUrl = "",
                onClick = {}
            )
        }
    }
}