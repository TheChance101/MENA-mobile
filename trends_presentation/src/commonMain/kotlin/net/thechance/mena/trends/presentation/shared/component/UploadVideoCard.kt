package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.available_video_format
import mena.trends_presentation.generated.resources.ic_trend_upload
import mena.trends_presentation.generated.resources.thumbnail_description
import mena.trends_presentation.generated.resources.upload_your_video
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.component.modifier.dashedBorder
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun UploadVideoCard(
    modifier: Modifier = Modifier,
    thumbnail: ByteArray? = null,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    onCardClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
) {

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .clip(RoundedCornerShape(Theme.radius.xl))
                .background(color = Theme.colorScheme.background.surfaceLow)
                .dashedBorder(color = Theme.colorScheme.brand.brand, cornerRadius = Theme.radius.xl)
                .noRippleClickable(
                    enabled = isEnabled,
                    onClick = onCardClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                ThumbnailLoading(
                    modifier = Modifier.matchParentSize()
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(Res.drawable.ic_trend_upload),
                        contentDescription = null,
                        tint = Theme.colorScheme.brand.brand
                    )
                    Text(
                        modifier = Modifier.padding(top = Theme.spacing._12),
                        text = stringResource(Res.string.upload_your_video),
                        style = Theme.typography.label.medium,
                        color = Theme.colorScheme.primary.primary
                    )
                    Text(
                        modifier = Modifier.padding(top = Theme.spacing._4),
                        text = stringResource(Res.string.available_video_format),
                        style = Theme.typography.label.extraSmall,
                        color = Theme.colorScheme.shadeSecondary
                    )
                }
            }

            thumbnail?.let {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = thumbnail,
                    contentDescription = stringResource(Res.string.thumbnail_description),
                    contentScale = ContentScale.Crop
                )
            }
        }
        thumbnail?.let {
            EditButton(
                modifier = Modifier
                    .offset(y = 16.dp)
                    .align(Alignment.BottomCenter),
                onClick = onEditClick
            )
        }
    }
}

@Composable
private fun ThumbnailLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .dashedBorder(color = Theme.colorScheme.brand.brand, cornerRadius = Theme.radius.xl)
            .background(color = Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator(
            dotSize = 12.dp,
            spaceBetween = 4.dp,
            numberOfDots = 4,
        )
    }
}

@Preview
@Composable
private fun UploadVideoCardPreview() {
    MenaTheme { UploadVideoCard() }
}