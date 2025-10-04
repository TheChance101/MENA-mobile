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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.arrow_reload_horizontal
import mena.trends_presentation.generated.resources.error
import mena.trends_presentation.generated.resources.ic_cancel
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_video
import mena.trends_presentation.generated.resources.loading
import mena.trends_presentation.generated.resources.retry
import mena.trends_presentation.generated.resources.success
import mena.trends_presentation.generated.resources.thumbnail
import mena.trends_presentation.generated.resources.upload_failed
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState
import net.thechance.mena.trends.presentation.shared.model.VideoAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VideoLoadingCardItem(
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
                .clip(RoundedCornerShape(Theme.spacing._12))
                .background(Theme.colorScheme.primary.onPrimary)
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
                    Icon(
                        painter = painterResource(Res.drawable.ic_cancel),
                        contentDescription = stringResource(Res.string.loading),
                        tint = Theme.colorScheme.shadeSecondary,
                        modifier = Modifier
                            .padding(top = Theme.spacing._12)
                            .size(Theme.spacing._16)
                            .clickable { onAction(VideoAction.Cancel) }
                    )
                }

                UploadReelScreenState.UploadingTrendState.FAILED -> {
                    Row(
                        modifier = Modifier.padding(top = Theme.spacing._24),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._16),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.error),
                            tint = Theme.colorScheme.shadeSecondary,
                            modifier = Modifier
                                .size(Theme.spacing._16)
                                .clickable { onAction(VideoAction.Delete) }
                        )
                        Icon(
                            painter = painterResource(Res.drawable.arrow_reload_horizontal),
                            contentDescription = stringResource(Res.string.retry),
                            tint = Theme.colorScheme.shadeSecondary,
                            modifier = Modifier
                                .size(Theme.spacing._16)
                                .clickable { onAction(VideoAction.Retry) }
                        )
                    }
                }

                UploadReelScreenState.UploadingTrendState.SUCCESS -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.success),
                        tint = Theme.colorScheme.shadeSecondary,
                        modifier = Modifier
                            .padding(top = Theme.spacing._24)
                            .size(Theme.spacing._16)
                            .clickable { onAction(VideoAction.Delete) }
                    )
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
        Icon(
            painter = painterResource(Res.drawable.ic_video),
            contentDescription = stringResource(Res.string.thumbnail),
            tint = Theme.colorScheme.brand.brand,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.brand.brandVariant)
                .padding(Theme.spacing._8)
        )

        Column(
            modifier = Modifier.padding(bottom = Theme.spacing._12)
        ) {
            Text(
                text = title,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.primary,
                modifier = Modifier.padding(
                    start = Theme.spacing._8,
                    bottom = Theme.spacing._4
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            when (videoState) {
                UploadReelScreenState.UploadingTrendState.UPLOADING -> {
                    Text(
                        text = videoSize,
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.extraSmall,
                        modifier = Modifier.padding(
                            start = Theme.spacing._8,
                            bottom = Theme.spacing._4
                        )
                    )
                    ProgressBar(
                        modifier = Modifier
                            .padding(
                                start = Theme.spacing._8,
                                end = Theme.spacing._12
                            )
                            .fillMaxWidth(),
                        color = Theme.colorScheme.brand.brand,
                        progress = {progress}
                    )
                }

                UploadReelScreenState.UploadingTrendState.SUCCESS -> {
                    Text(
                        text = videoSize,
                        color = Theme.colorScheme.shadeSecondary,
                        style = Theme.typography.label.extraSmall,
                        modifier = Modifier.padding(start = Theme.spacing._8)
                    )
                }

                UploadReelScreenState.UploadingTrendState.FAILED -> {
                    Text(
                        text = stringResource(Res.string.upload_failed),
                        color = Theme.colorScheme.border.error,
                        style = Theme.typography.label.extraSmall,
                        modifier = Modifier.padding(
                            start = Theme.spacing._8,
                            end = Theme.spacing._8
                        )
                    )
                }

                UploadReelScreenState.UploadingTrendState.IDLE -> {}
            }
        }
    }
}