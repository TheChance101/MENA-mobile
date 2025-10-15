package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import app.cash.paging.compose.itemKey
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.favorite
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_placeholder_profile
import mena.trends_presentation.generated.resources.manage_trends_title
import mena.trends_presentation.generated.resources.my_trends
import mena.trends_presentation.generated.resources.profile_image_desc
import mena.trends_presentation.generated.resources.trend_image_desc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.segment.Segment
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
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
        topBar = { ManageMyTrendsAppBar(onBackClick = listener::onBackClick) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            UserAvatar(
                profileImageUrl = state.profile.profileImageUrl,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = state.profile.userName,
                style = Theme.typography.label.medium,
                modifier = Modifier
                    .padding(top = Theme.spacing._8, bottom = Theme.spacing._32)
                    .align(Alignment.CenterHorizontally)
            )

            SegmentSection(
                reels = state.reels.collectAsLazyPagingItems(),
                onTrendClick = listener::onReelClick,
                modifier = Modifier.weight(1f).fillMaxWidth(),
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16)
    ) {
        Segment(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item(title = trendsTitle) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 106.dp),
                    state = rememberLazyGridState(),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(space = Theme.spacing._4),
                    horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._4, Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(bottom = Theme.spacing._16)
                ) {
                    items(key = reels.itemKey(), count = reels.itemCount) { index ->
                        reels[index]?.let { reel ->
                            TrendItem(
                                item = reel,
                                onTrendClick = onTrendClick
                            )
                        }
                    }
                }
            }

            item(favoriteTitle) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No favorites yet", style = Theme.typography.body.small)
                }
            }
        }
    }
}


@Composable
private fun TrendItem(
    item: ReelUiState,
    onTrendClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 106.dp, height = 164.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrendClick(item.id) }
            .background(color = Theme.colorScheme.background.surfaceHigh)
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = stringResource(resource = Res.string.trend_image_desc),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}