package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.favorite
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_paly_now
import mena.trends_presentation.generated.resources.ic_placeholder_profile
import mena.trends_presentation.generated.resources.manage_trends_title
import mena.trends_presentation.generated.resources.my_trends
import mena.trends_presentation.generated.resources.no_favorites_yet
import mena.trends_presentation.generated.resources.play_now
import mena.trends_presentation.generated.resources.profile_image_desc
import mena.trends_presentation.generated.resources.trend_image_desc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.segment.Segment
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ManageTrendsScreen(
    viewModel: ManageTrendsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is ManageTrendsUiEffect.NavigateBack -> navController.navigateUp()
            is ManageTrendsUiEffect.NavigateToTrend -> {
                navController.navigate(Route.ReelDetails(effect.reelId))
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getReels()
    }

    ManageTrendsScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun ManageTrendsScreenContent(
    state: ManageTrendsScreenState,
    listener: ManageTrendsInteractionListener
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = state.isLoading.not(),
                content = { ManageMyTrendsAppBar(onBackClick = listener::onClickBack) })
        },
        content = {
            AnimatedVisibility(
                visible = state.isLoading,
                content = { LoadingProgressBar() }
            )

            AnimatedVisibility(
                visible = state.error == ErrorState.NoInternet,
                content = { NoConnection { listener.onClickRetry() } }
            )

            AnimatedVisibility(
                visible = state.error == null && state.isLoading.not(),
                content = { ManageTrendsScreenBody(listener, state) }
            )
        }
    )
}

@Composable
private fun ManageTrendsScreenBody(
    listener: ManageTrendsInteractionListener,
    state: ManageTrendsScreenState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatar(
                    profileImageUrl = state.profile.profileImageUrl,
                )

                Text(
                    text = state.profile.userName,
                    style = Theme.typography.label.medium,
                    modifier = Modifier
                        .padding(top = Theme.spacing._8, bottom = Theme.spacing._32)
                )
            }
        }

        item {
            SegmentSection(
                reels = state.reels.collectAsLazyPagingItems(),
                onTrendClick = listener::onClickReel,
                modifier = Modifier.fillMaxWidth(),
                trendsTitle = stringResource(Res.string.my_trends),
                favoriteTitle = stringResource(Res.string.favorite)
            )
        }

    }
}

@Composable
private fun ManageMyTrendsAppBar(onBackClick: () -> Unit) {
    AppBar(
        onLeadingClick = onBackClick,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        title = stringResource(Res.string.manage_trends_title),
    )
}

@Composable
private fun UserAvatar(profileImageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = profileImageUrl,
        contentDescription = stringResource(Res.string.profile_image_desc),
        error = painterResource(Res.drawable.ic_placeholder_profile),
        modifier = modifier.padding(top = 32.dp).size(100.dp).clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SegmentSection(
    reels: LazyPagingItems<ReelUiState>,
    trendsTitle: String,
    favoriteTitle: String,
    onTrendClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val totalWidth = maxWidth
        val itemMinWidth = 106.dp
        val columnsCount = (totalWidth / (itemMinWidth + Theme.spacing._4)).toInt().coerceAtLeast(1)
        val rowsCount = (reels.itemCount + columnsCount - 1) / columnsCount

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._16)
        ) {
            Segment(modifier = Modifier.fillMaxWidth()) {

                item(trendsTitle) {
                    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)) {
                        repeat(rowsCount) { rowIndex ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (colIndex in 0 until columnsCount) {
                                    val index = rowIndex * columnsCount + colIndex
                                    if (index < reels.itemCount) {
                                        reels[index]?.let { reel ->
                                            TrendItem(
                                                item = reel,
                                                onTrendClick = onTrendClick,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                            )
                                        } ?: PlaceholderBox(modifier = Modifier.weight(1f))
                                    } else {
                                        PlaceholderBox(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        if (reels.loadState.append is LoadState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DotsProgressIndicator()
                            }
                        }
                    }
                }

                item(favoriteTitle) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Theme.spacing._16),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.no_favorites_yet),
                            style = Theme.typography.body.small
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PlaceholderBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(106f / 164f)
    )
}

@Composable
private fun TrendItem(
    item: ReelUiState,
    onTrendClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(106f / 164f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrendClick(item.id) }
            .background(color = Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = item.thumbnailUrl.isNotEmpty()) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = stringResource(resource = Res.string.trend_image_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        AnimatedVisibility(visible = item.thumbnailUrl.isEmpty()) {
            Icon(
                painter = painterResource(Res.drawable.ic_paly_now),
                contentDescription = stringResource(Res.string.play_now),
                tint = Theme.colorScheme.primary.onPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = Theme.colorScheme.shadeTertiary)
                    .padding(7.dp)
            )
        }
    }
}