package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.segment.Segment
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
        modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._32)
                )
            }
        }

        item {
            SegmentSectionWithGrid(
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
private fun SegmentSectionWithGrid(
    reels: LazyPagingItems<ReelUiState>,
    trendsTitle: String,
    favoriteTitle: String,
    onTrendClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = Theme.spacing._16)
    ) {
        Segment(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item(title = trendsTitle) {
                val itemHeight = 164.dp
                val itemWidth = 106.dp
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = Theme.spacing._4,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)
                ) {
                    repeat(reels.itemCount) { index ->
                        reels[index]?.let { reel ->
                            TrendItem(
                                item = reel,
                                onTrendClick = onTrendClick,
                                modifier = Modifier.size(width = itemWidth, height = itemHeight)
                            )
                        }
                    }
                }
            }

            item(favoriteTitle) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing._32),
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

@Preview(showBackground = true)
@Composable
private fun ManageMyTrendsPreview() {
    MenaTheme {
        ManageTrendsScreenBody(
            state = ManageTrendsScreenState(),
            listener = object : ManageTrendsInteractionListener {
                override fun onClickReel(reelId: String) {}
                override fun onClickBack() {}
                override fun onClickRetry() {}
            }
        )
    }
}