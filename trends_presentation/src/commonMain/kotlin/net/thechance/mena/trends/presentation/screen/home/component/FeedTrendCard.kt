package net.thechance.mena.trends.presentation.screen.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_eye
import mena.trends_presentation.generated.resources.ic_heart
import mena.trends_presentation.generated.resources.ic_paly_now
import mena.trends_presentation.generated.resources.just_now
import mena.trends_presentation.generated.resources.likes
import mena.trends_presentation.generated.resources.likes_suffix
import mena.trends_presentation.generated.resources.play_now
import mena.trends_presentation.generated.resources.profile_image
import mena.trends_presentation.generated.resources.video_thumbnail
import mena.trends_presentation.generated.resources.views
import mena.trends_presentation.generated.resources.views_suffix
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.screen.home.TrendUiState
import net.thechance.mena.trends.presentation.shared.component.BaseAsyncImage
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.util.asString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedTrendCard(
    trend: TrendUiState,
    onClickLike: () -> Unit,
    onClickTrend: () -> Unit,
    onRequestRefresh: () -> Unit,
    onExpandDescription: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(bottom = Theme.spacing._12)
    ) {
        TrendHeaderSection(
            trend = trend,
            timeAgoText = trend.timeAgo?.asString() ?: stringResource(Res.string.just_now),
            onClickTrend = onClickTrend,
            onRequestRefresh = onRequestRefresh
        )

        TrendFooterSection(
            trend = trend,
            onClickLike = onClickLike,
            onExpandDescription = onExpandDescription
        )
    }
}

@Composable
private fun TrendHeaderSection(
    trend: TrendUiState,
    timeAgoText: String,
    onRequestRefresh: () -> Unit,
    onClickTrend: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = trend.profileImageUrl,
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
                )
                Text(
                    text = timeAgoText,
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BaseAsyncImage(
                url = trend.thumbnailUrl,
                contentDescription = stringResource(Res.string.video_thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Theme.colorScheme.background.surfaceHigh)
                    .noRippleClickable { onClickTrend() },
                alignment = Alignment.Center,
                onRequestRefresh = onRequestRefresh,
                imageCacheKey = trend.id
            )
            Icon(
                painter = painterResource(Res.drawable.ic_paly_now),
                contentDescription = stringResource(Res.string.play_now),
                tint = Theme.colorScheme.primary.onPrimary,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = Theme.colorScheme.primary.onPrimaryHint)
                    .padding(14.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun TrendFooterSection(
    trend: TrendUiState,
    onClickLike: () -> Unit,
    onExpandDescription: (String) -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val likeIconColor by animateColorAsState(
        targetValue = if (trend.isLiked) Theme.colorScheme.error else Theme.colorScheme.shadeTertiary,
        animationSpec = tween(durationMillis = 500)
    )

    if (trend.description.isNotBlank()) {
        ExpandableText(
            text = trend.description,
            isExpanded = trend.isDescriptionExpanded,
            modifier = Modifier.padding(Theme.spacing._12),
            onExpandedChange = { onExpandDescription(trend.id) }
        )
    }

    AnimatedVisibility(
        visible = trend.description.isBlank(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
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
                    .scale(scale.value)
                    .noRippleClickable {
                        onClickLike()
                        scope.launch {
                            scale.animateTo(1.4f, tween(200))
                            scale.animateTo(1f, tween(200))
                        }
                    }
            )

            Text(
                text = pluralStringResource(
                    resource = Res.plurals.likes_suffix,
                    quantity = trend.likesCount,
                    formatArgs = arrayOf(trend.likesCount)
                ),
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
                text = pluralStringResource(
                    resource = Res.plurals.views_suffix,
                    quantity = trend.viewsCount,
                    formatArgs = arrayOf(trend.viewsCount)
                ),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}