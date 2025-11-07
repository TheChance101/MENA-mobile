package net.thechance.mena.trends.presentation.screen.home

import androidx.compose.animation.AnimatedVisibility
import app.cash.paging.compose.itemKey
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.add_reel
import mena.trends_presentation.generated.resources.edit_tags
import mena.trends_presentation.generated.resources.ic_account_setting
import mena.trends_presentation.generated.resources.ic_add_real
import mena.trends_presentation.generated.resources.ic_pencil_edit
import mena.trends_presentation.generated.resources.manage_trends
import mena.trends_presentation.generated.resources.trends_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.home.component.EmptyTrends
import net.thechance.mena.trends.presentation.screen.home.component.FeedReelCard
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.base.toErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is HomeUiEffect.NavigateToReelDetails ->
                navController.navigate(Route.ReelDetails(effect.trendId, isFromHome = true))

            is HomeUiEffect.NavigateToAddReel ->
                navController.navigate(Route.UploadReel)

            is HomeUiEffect.NavigateToChangeTags ->
                navController.navigate(Route.UpdateCategories)

            is HomeUiEffect.NavigateToManageMyTrends ->
                navController.navigate(Route.ManageReels)
        }
    }

    LaunchedEffect(Unit) { viewModel.getFeedReels() }

    HomeScreenContent(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeScreenState,
    listener: HomeInteractionListener,
) {
    Scaffold(
        topBar = {
            TrendsAnimatedVisibility(visible = state.isLoading.not()) {
                TrendsAppBar(
                    onClickManageMyTrends = listener::onClickManageMyTrends,
                    onClickEditTags = listener::onClickEditTags
                )
            }
        }
    ) {
        val reels = state.reels.collectAsLazyPagingItems()
        val listState = rememberLazyListState()

        val hasNetworkError = reels.loadState.refresh.toErrorState() == ErrorState.NoInternet
                && reels.itemSnapshotList.isEmpty()

        val shouldShowEmptyState = reels.itemSnapshotList.isEmpty() &&
                reels.loadState.refresh is LoadState.NotLoading &&
                reels.loadState.refresh.toErrorState() == null

        Box(modifier = Modifier.fillMaxSize()) {
            TrendsAnimatedVisibility(
                visible = reels.loadState.refresh is LoadState.Loading,
                content = { LoadingProgressBar() }
            )

            TrendsAnimatedVisibility(
                visible = hasNetworkError,
                content = { NoConnection { listener.onClickRetry() } }
            )

            TrendsAnimatedVisibility(
                visible = shouldShowEmptyState,
                content = { EmptyTrends() }
            )

            TrendsAnimatedVisibility(
                visible = reels.itemSnapshotList.isNotEmpty() && reels.loadState.refresh !is LoadState.Loading,
                content = {
                    ReelsListSection(
                        reels = reels,
                        onClickLike = listener::onClickLike,
                        onClickReel = listener::onClickReel,
                        onExpandDescription = listener::onClickExpandDescription,
                        listState = listState
                    )
                }
            )

            TrendsAnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomEnd),
                visible = !hasNetworkError && reels.loadState.refresh !is LoadState.Loading,
                content = { AddTrendFAB(onClickFab = { listener.onClickAddReel() }) }
            )
        }
    }
}

@Composable
private fun AddTrendFAB(
    modifier: Modifier = Modifier,
    onClickFab: () -> Unit
) {
    Icon(
        painter = painterResource(Res.drawable.ic_add_real),
        contentDescription = stringResource(Res.string.add_reel),
        modifier = modifier
            .padding(end = Theme.spacing._16, bottom = Theme.spacing._16)
            .size(56.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.primary.primary)
            .noRippleClickable { onClickFab() }
            .padding(Theme.spacing._16),
    )
}

@Composable
private fun ReelsListSection(
    reels: LazyPagingItems<ReelUiState>,
    listState: LazyListState,
    onClickLike: (reelId: String, isLiked: Boolean) -> Unit,
    onClickReel: (reelId: String) -> Unit,
    onExpandDescription: (reelId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(vertical = Theme.spacing._8,horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._16)
    ) {
        items(
            count = reels.itemCount,
            key = reels.itemKey { it.id }
        ) { index ->
            reels[index]?.let { reel ->
                FeedReelCard(
                    reel = reel,
                    onClickLike = { onClickLike(reel.id, reel.isLiked) },
                    onClickReel = { onClickReel(reel.id) },
                    onExpandDescription = { onExpandDescription(reel.id) }
                )
            }
        }
    }
}

@Composable
private fun TrendsAppBar(
    onClickManageMyTrends: () -> Unit,
    onClickEditTags: () -> Unit
) {
    AppBar(
        title = stringResource(Res.string.trends_title),
        trailingContent = {
            AppBarOptionContainer(
                isBadgeVisible = false,
                onClick = onClickManageMyTrends
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_account_setting),
                    contentDescription = stringResource(Res.string.manage_trends),
                    tint = Theme.colorScheme.shadePrimary
                )
            }

            AppBarOptionContainer(
                isBadgeVisible = false,
                onClick = onClickEditTags
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = stringResource(Res.string.edit_tags),
                    tint = Theme.colorScheme.shadePrimary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MenaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            HomeScreenContent(
                state = HomeScreenState(),
                listener = object : HomeInteractionListener {
                    override fun onClickLike(reelId: String, isLiked: Boolean) {}
                    override fun onClickAddReel() {}
                    override fun onClickEditTags() {}
                    override fun onClickManageMyTrends() {}
                    override fun onClickReel(reelId: String) {}
                    override fun onClickRetry() {}
                    override fun onClickExpandDescription(reelId: String) {}
                }
            )
        }
    }
}