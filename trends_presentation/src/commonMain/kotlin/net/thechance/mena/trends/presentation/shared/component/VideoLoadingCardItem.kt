package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.error
import mena.trends_presentation.generated.resources.ic_arrow_reload_horizontal
import mena.trends_presentation.generated.resources.ic_cancel
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_video
import mena.trends_presentation.generated.resources.loading
import mena.trends_presentation.generated.resources.retry
import mena.trends_presentation.generated.resources.thumbnail
import mena.trends_presentation.generated.resources.upload_failed
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState
import net.thechance.mena.trends.presentation.shared.model.VideoAction
import net.thechance.mena.trends.presentation.shared.util.isFailed
import net.thechance.mena.trends.presentation.shared.util.isSuccess
import net.thechance.mena.trends.presentation.shared.util.isUploading
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VideoLoadingCardItem(
    title: String,
    sizeUploaded: String,
    videoSize: String,
    uploadingState: UploadReelScreenState.UploadingReelState,
    progress: Float,
    modifier: Modifier = Modifier,
    onAction: (VideoAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.primary.onPrimary)
            .padding(
                top = Theme.spacing._12,
                start = Theme.spacing._12,
                end = Theme.spacing._12,
                bottom = 14.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.brand.brandVariant)
                .padding(Theme.spacing._8),
            painter = painterResource(Res.drawable.ic_video),
            contentDescription = stringResource(Res.string.thumbnail),
            tint = Theme.colorScheme.brand.brand
        )

        Column(Modifier.padding(start = Theme.spacing._8)) {
            Row(Modifier.heightIn(min = 40.dp)) {
            VideoInfoSection(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(end = Theme.spacing._16),
                    title = title,
                    sizeUploaded = sizeUploaded,
                    videoSize = videoSize,
                    uploadingState = uploadingState
                )

                VideoActionsSection(
                    modifier = Modifier.fillMaxHeight(),
                    uploadingState = uploadingState,
                    onAction = onAction
                )
            }
            AnimatedVisibility(
                visible = uploadingState.isUploading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ProgressBar(
                    modifier = Modifier
                        .padding(top = Theme.spacing._4)
                        .fillMaxWidth(),
                    color = Theme.colorScheme.brand.brand,
                    progress = { progress }
                )
            }
        }
    }
}

@Composable
private fun VideoInfoSection(
    title: String,
    sizeUploaded: String,
    videoSize: String,
    uploadingState: UploadReelScreenState.UploadingReelState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.primary.primary,
            maxLines = 1
        )

        when (uploadingState) {
            UploadReelScreenState.UploadingReelState.UPLOADING,
            UploadReelScreenState.UploadingReelState.SUCCESS -> {
                Text(
                    text = "$sizeUploaded/$videoSize",
                    color = Theme.colorScheme.shadeSecondary,
                    style = Theme.typography.label.extraSmall
                )

            }

            UploadReelScreenState.UploadingReelState.FAILED -> {
                Text(
                    text = stringResource(Res.string.upload_failed),
                    color = Theme.colorScheme.border.error,
                    style = Theme.typography.label.extraSmall
                )
            }

            UploadReelScreenState.UploadingReelState.IDLE -> {}
        }
    }
}

@Composable
private fun VideoActionsSection(
    uploadingState: UploadReelScreenState.UploadingReelState,
    modifier: Modifier = Modifier,
    onAction: (VideoAction) -> Unit
) {
    Box(modifier = modifier) {
        AnimatedVisibility (
            visible = uploadingState.isUploading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(Theme.spacing._16)
                    .clickable { onAction(VideoAction.Cancel) },
                painter = painterResource(Res.drawable.ic_cancel),
                contentDescription = stringResource(Res.string.loading),
                tint = Theme.colorScheme.shadeSecondary
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._16),
        ) {
            AnimatedVisibility (
                visible = uploadingState.isFailed || uploadingState.isSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    modifier = Modifier
                        .size(Theme.spacing._16)
                        .clickable { onAction(VideoAction.Delete) },
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = stringResource(Res.string.error),
                    tint = Theme.colorScheme.shadeSecondary
                )
            }
            AnimatedVisibility (
                visible = uploadingState.isFailed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_reload_horizontal),
                    contentDescription = stringResource(Res.string.retry),
                    tint = Theme.colorScheme.shadeSecondary,
                    modifier = Modifier
                        .size(Theme.spacing._16)
                        .clickable { onAction(VideoAction.Retry) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoUploadSuccessPreview() {
    MenaTheme {
        VideoLoadingCardItem(
            title = "Upload Reel",
            sizeUploaded = "20 MB",
            videoSize = "20 MB",
            uploadingState = UploadReelScreenState.UploadingReelState.SUCCESS,
            progress = 1f,
            onAction = {}
        )
    }
}
