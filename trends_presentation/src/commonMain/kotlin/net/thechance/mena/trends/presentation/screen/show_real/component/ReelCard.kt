package net.thechance.mena.trends.presentation.screen.show_real.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_eye
import mena.trends_presentation.generated.resources.ic_heart
import mena.trends_presentation.generated.resources.just_now
import mena.trends_presentation.generated.resources.likes
import mena.trends_presentation.generated.resources.likes_suffix
import mena.trends_presentation.generated.resources.profile_image
import mena.trends_presentation.generated.resources.video_thumbnail
import mena.trends_presentation.generated.resources.views
import mena.trends_presentation.generated.resources.views_suffix
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.show_real.ReelUiState
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.util.asString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedReelCard(
    reel: ReelUiState,
    onLikeClick: () -> Unit,
    onReelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(bottom = Theme.spacing._12)
    ) {
        ReelHeaderSection(
            reel = reel,
            timeAgoText = reel.timeAgo?.asString() ?: stringResource(Res.string.just_now),
            onReelClick = onReelClick
        )

        ReelFooterSection(
            reel = reel,
            onLikeClick = onLikeClick
        )
    }
}

@Composable
private fun ReelHeaderSection(
    reel: ReelUiState,
    timeAgoText: String,
    onReelClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = reel.profileImageUrl,
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
                    text = reel.userName,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary,
                    maxLines = 1,
                )
                Text(
                    text = timeAgoText,
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
        }

        AsyncImage(
            model = reel.thumbnailUrl,
            contentDescription = stringResource(Res.string.video_thumbnail),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Theme.colorScheme.background.surfaceHigh)
                .noRippleClickable { onReelClick() },
            alignment = Alignment.Center
        )
    }
}

@Composable
private fun ReelFooterSection(
    reel: ReelUiState,
    onLikeClick: () -> Unit
) {

    val likeIconColor by animateColorAsState(
        targetValue = if (reel.isLiked) Theme.colorScheme.error else Theme.colorScheme.shadeTertiary,
        animationSpec = tween(durationMillis = 500)
    )

    if (reel.description.isNotBlank()) {
        Text(
            text = reel.description,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._12)
        )
    } else {
        Spacer(modifier = Modifier.height(Theme.spacing._12))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._12),
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
                tint = likeIconColor,
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable { onLikeClick() }
            )
            Text(
                text = stringResource(Res.string.likes_suffix, reel.likesCount),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_eye),
                contentDescription = stringResource(Res.string.views),
                tint = Theme.colorScheme.shadeTertiary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(Res.string.views_suffix, reel.viewsCount),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}