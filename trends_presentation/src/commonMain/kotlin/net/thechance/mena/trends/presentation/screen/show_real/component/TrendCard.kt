package net.thechance.mena.trends.presentation.screen.show_real.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_eyee
import mena.trends_presentation.generated.resources.ic_dots
import mena.trends_presentation.generated.resources.ic_heart
import mena.trends_presentation.generated.resources.just_now
import mena.trends_presentation.generated.resources.likes
import mena.trends_presentation.generated.resources.likes_suffix
import mena.trends_presentation.generated.resources.more_options
import mena.trends_presentation.generated.resources.profile_image
import mena.trends_presentation.generated.resources.video_thumbnail
import mena.trends_presentation.generated.resources.views
import mena.trends_presentation.generated.resources.views_suffix
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.show_real.TrendsScreenState
import net.thechance.mena.trends.presentation.screen.show_real.extention.toTimeAgo
import net.thechance.mena.trends.presentation.shared.util.isValidImageUrl
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TrendCard(
    trend: TrendsScreenState.TrendUiState,
    onMoreClick: () -> Unit,
    onLikeClick: () -> Unit,
    onVideoClick: (String) -> Unit
) {
    val timeAgoText = trend.timeAgo?.toTimeAgo() ?: stringResource(Res.string.just_now)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(bottom = Theme.spacing._12)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (isValidImageUrl(trend.profileImageUrl)) trend.profileImageUrl else null,
                contentDescription = stringResource(Res.string.profile_image),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Theme.spacing._8)
            ) {
                Text(
                    text = trend.userName,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeAgoText,
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }

            Icon(
                painter = painterResource(Res.drawable.ic_dots),
                contentDescription = stringResource(Res.string.more_options),
                tint = Theme.colorScheme.shadeTertiary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onMoreClick)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Theme.colorScheme.background.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (isValidImageUrl(trend.thumbnailUrl)) trend.thumbnailUrl else null,
                contentDescription = stringResource(Res.string.video_thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onVideoClick(trend.videoUrl) }
            )
        }

        if (trend.description.isNotEmpty()) {
            Text(
                text = trend.description,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier
                    .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._12)
            )
        } else {
            Box(modifier = Modifier.height(Theme.spacing._12))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._12)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = stringResource(Res.string.likes),
                    tint = Theme.colorScheme.shadeTertiary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onLikeClick() }
                )
                Text(
                    text = stringResource(Res.string.likes_suffix, trend.likes),
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_eyee),
                    contentDescription = stringResource(Res.string.views),
                    tint = Theme.colorScheme.shadeTertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(Res.string.views_suffix, trend.views),
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
        }
    }
}