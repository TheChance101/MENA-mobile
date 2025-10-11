package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.category_icon
import mena.dukan_presentation.generated.resources.ic_error
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
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    val isLoading = remember { mutableStateOf(true) }
    val isError = remember { mutableStateOf(false) }
    val isCategoryButtonEnabled = when {
        isLoading.value -> false
        isError.value -> false
        else -> true
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = Theme.spacing._4)
                .size(size = 60.dp)
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = RoundedCornerShape(Theme.radius.full)
                ).clip(shape = RoundedCornerShape(Theme.radius.full))
                .clickable(onClick = onClick, enabled = isCategoryButtonEnabled)
                .skeletonLoading(isLoading = isLoading.value),
            contentAlignment = Alignment.Center
        ) {
            val imageRequest = ImageRequest.Builder(LocalPlatformContext.current)
                .data(imageUrl)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()

            val iconTintColor = when {
                isError.value -> Theme.colorScheme.error
                else -> Theme.colorScheme.primary.primary
            }

            AsyncImage(
                model = imageRequest,
                contentDescription = stringResource(resource = Res.string.category_icon),
                modifier = Modifier.size(iconSize),
                colorFilter = ColorFilter.tint(color = iconTintColor),
                onSuccess = {
                    isLoading.value = false
                    isError.value = false
                },
                onError = {
                    isLoading.value = false
                    isError.value = true
                },
                error = painterResource(Res.drawable.ic_error),
            )
        }

        Text(
            text = title,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            minLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._8)
                .skeletonLoading(isLoading =isLoading.value)
        )
    }
}

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