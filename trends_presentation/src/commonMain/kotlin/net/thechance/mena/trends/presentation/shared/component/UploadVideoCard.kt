package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.available_video_format
import mena.trends_presentation.generated.resources.ic_trend_upload
import mena.trends_presentation.generated.resources.upload_your_video
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
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
    thumbnail: Painter? = null,
    isEnabled: Boolean = true,
    onCardClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .clip(
                    RoundedCornerShape(Theme.radius.xl))
                .background(color = Theme.colorScheme.background.surfaceLow)
                .then(
                    thumbnail?.let {
                        Modifier.dashedBorder(color = Theme.colorScheme.brand.brand, cornerRadius = Theme.radius.xl)
                    } ?: Modifier
                )
                .noRippleClickable(
                    enabled = isEnabled,
                    onClick = onCardClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(vertical = 71.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_trend_upload),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(bottom = Theme.spacing._12)
                        .size(40.dp)
                )
                Text(
                    text = stringResource(Res.string.upload_your_video),
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.primary.primary,
                    modifier = Modifier.padding(bottom = Theme.spacing._4)
                )
                Text(
                    text = stringResource(Res.string.available_video_format),
                    style = Theme.typography.label.extraSmall,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
            thumbnail?.let{
                Image(
                    painter = thumbnail,
                    contentDescription = stringResource(Res.string.thumbnail_description),
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        thumbnail?.let{
            EditButton(
                modifier = Modifier
                    .offset(y = 16.dp)
                    .align(Alignment.BottomCenter),
                isClickEnabled =  thumbnail != null,
                onClick = onEditClick
            )
        }
    }
}

@Composable
@Preview
private fun UploadVideoCardPreview() {
    MenaTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            UploadVideoCard()
        }
    }
}