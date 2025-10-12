package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.acton_icon_description
import mena.trends_presentation.generated.resources.arrow_reload_horizontal
import mena.trends_presentation.generated.resources.ic_cancel
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_video
import mena.trends_presentation.generated.resources.thumbnail
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState
import net.thechance.mena.trends.presentation.shared.model.VideoAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun VideoLoadingCardItem(
    title: String,
    videoSize: String,
    videoState: UploadReelScreenState.UploadingTrendState,
    progress: Float,
    modifier: Modifier = Modifier,
    onAction: (VideoAction) -> Unit
) {
    if (videoState != UploadReelScreenState.UploadingTrendState.IDLE) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = Theme.spacing._12))
                .background(color = Theme.colorScheme.primary.onPrimary)
                .padding(horizontal = Theme.spacing._12),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VideoInfoSection(
                title = title,
                videoSize = videoSize,
                videoState = videoState,
                progress = progress,
                modifier = Modifier.weight(1f)
            )

            when (videoState) {
                UploadReelScreenState.UploadingTrendState.UPLOADING -> {
                    ActionIcon(
                        actinIcon = painterResource(resource = Res.drawable.ic_cancel),
                        modifier = Modifier.padding(top = Theme.spacing._12)
                    ) {
                        onAction(VideoAction.Cancel)
                    }
                }

                UploadReelScreenState.UploadingTrendState.FAILED -> {
                    Row(
                        modifier = Modifier.padding(top = Theme.spacing._24),
                        horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._16),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIcon(actinIcon = painterResource(resource = Res.drawable.ic_delete)) {
                            onAction(VideoAction.Delete)
                        }
                        ActionIcon(actinIcon = painterResource(resource = Res.drawable.arrow_reload_horizontal)) {
                            onAction(VideoAction.Retry)
                        }
                    }
                }

                UploadReelScreenState.UploadingTrendState.SUCCESS -> {
                    ActionIcon(
                        actinIcon = painterResource(resource = Res.drawable.ic_delete),
                        modifier = Modifier.padding(top = Theme.spacing._24)
                    ) {
                        onAction(VideoAction.Delete)
                    }
                }

                UploadReelScreenState.UploadingTrendState.IDLE -> {}
            }
        }
    }
}

@Composable
private fun VideoInfoSection(
    title: String,
    videoSize: String,
    videoState: UploadReelScreenState.UploadingTrendState,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(top = Theme.spacing._12),
        verticalAlignment = Alignment.Top
    ) {
        VideoIcon()

        Column(
            modifier = Modifier.padding(bottom = Theme.spacing._12)
        ) {
            Text(
                text = title,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.primary,
                modifier = Modifier.padding(start = Theme.spacing._8, bottom = Theme.spacing._4),
                maxLines = 1
            )

            when (videoState) {
                UploadReelScreenState.UploadingTrendState.UPLOADING -> {
                    UploadVideoSize(
                        videoSize = videoSize,
                        modifier = Modifier.padding(bottom = Theme.spacing._4)
                    )
                    ProgressBar(
                        modifier = Modifier
                            .padding(start = Theme.spacing._8, end = Theme.spacing._12)
                            .fillMaxWidth(),
                        color = Theme.colorScheme.brand.brand,
                        progress = { progress }
                    )
                }

                UploadReelScreenState.UploadingTrendState.SUCCESS -> {
                    UploadVideoSize(videoSize = videoSize)
                }

                UploadReelScreenState.UploadingTrendState.FAILED -> {
                    UploadVideoSize(
                        videoSize = videoSize, modifier = Modifier.padding(end = Theme.spacing._8)
                    )
                }

                UploadReelScreenState.UploadingTrendState.IDLE -> {}
            }
        }
    }
}

@Composable
private fun ActionIcon(
    actinIcon: Painter,
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    Icon(
        painter = actinIcon,
        contentDescription = stringResource(resource = Res.string.acton_icon_description),
        tint = Theme.colorScheme.shadeSecondary,
        modifier = modifier
            .size(size = Theme.spacing._16)
            .clickable { onAction() }
    )
}

@Composable
private fun UploadVideoSize(videoSize: String, modifier: Modifier = Modifier) {
    Text(
        text = videoSize,
        color = Theme.colorScheme.shadeSecondary,
        style = Theme.typography.label.extraSmall,
        modifier = modifier.padding(start = Theme.spacing._8)
    )
}

@Composable
private fun VideoIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_video),
        contentDescription = stringResource(Res.string.thumbnail),
        tint = Theme.colorScheme.brand.brand,
        modifier = Modifier
            .size(size = 40.dp)
            .clip(shape = RoundedCornerShape(Theme.radius.md))
            .background(color = Theme.colorScheme.brand.brandVariant)
            .padding(all = Theme.spacing._8)
    )
}

@Preview
@Composable
private fun VideoLoadingCardItemPreview() {
    MenaTheme {
        VideoLoadingCardItem(
            title = "video",
            videoSize = "11",
            videoState = UploadReelScreenState.UploadingTrendState.UPLOADING,
            progress = 1f
        ) {}
    }
}