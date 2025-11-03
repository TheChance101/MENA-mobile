package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
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
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
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
            TrendsAnimatedVisibility(
                visible = state.isLoading.not(),
                content = { ManageMyTrendsAppBar(onBackClick = listener::onClickBack) }
            )
        },
        content = {
            TrendsAnimatedVisibility(
                visible = state.isLoading,
                content = { LoadingProgressBar() }
            )

            TrendsAnimatedVisibility(
                visible = state.error == ErrorState.NoInternet,
                content = { NoConnection { listener.onClickRetry() } }
            )

            TrendsAnimatedVisibility(
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
    val reels = state.reels.collectAsLazyPagingItems()
    val cardWidth = 106.dp

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cardWidth),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(space = Theme.spacing._4),
        horizontalArrangement = Arrangement.spacedBy(
            space = Theme.spacing._4,
            Alignment.CenterHorizontally
        ),
        contentPadding = PaddingValues(
            start = Theme.spacing._16,
            end = Theme.spacing._16,
            bottom = Theme.spacing._16
        )
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            UserAvatar(
                profileImageUrl = state.profile.profileImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.spacing._32, bottom = Theme.spacing._8)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = state.profile.userName,
                style = Theme.typography.label.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing._32)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .padding(bottom = Theme.spacing._16)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
            ) {
                SegmentButton(
                    title = stringResource(Res.string.my_trends),
                    isSelected = state.selectTab == SelectTab.MyTrends,
                    onSelectChange = { listener.onSelectTab(SelectTab.MyTrends) },
                    modifier = Modifier.weight(1f)
                )

                SegmentButton(
                    title = stringResource(Res.string.favorite),
                    isSelected = state.selectTab == SelectTab.Favorites,
                    onSelectChange = { listener.onSelectTab(SelectTab.Favorites) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.selectTab == SelectTab.Favorites) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(Res.string.no_favorites_yet),
                    style = Theme.typography.label.medium,
                    modifier = Modifier
                        .fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
            return@LazyVerticalGrid
        }//temporary until we make implementation for it

        items(key = reels.itemKey(), count = reels.itemCount) { index ->
            reels[index]?.let { reel ->
                TrendItem(
                    item = reel,
                    onTrendClick = listener::onClickReel
                )
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
        modifier = modifier.size(100.dp).clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun TrendItem(
    item: ReelUiState,
    onTrendClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidthRatio = 106f / 164f

    Box(
        modifier = modifier
            .aspectRatio(cardWidthRatio)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrendClick(item.id) }
            .background(color = Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center

    ) {
        TrendsAnimatedVisibility(visible = item.thumbnailUrl.isNotEmpty()) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = stringResource(resource = Res.string.trend_image_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        TrendsAnimatedVisibility(visible = item.thumbnailUrl.isEmpty()) {
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

@Composable
private fun SegmentButton(
    title: String,
    isSelected: Boolean,
    onSelectChange: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val animatedButtonColor = animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.background.surfaceHigh,
    )

    Text(
        text = title,
        style = Theme.typography.label.medium,
        color = if (isSelected) Theme.colorScheme.shadePrimary else Theme.colorScheme.shadeSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(4.dp)
            .shadow(
                if (isSelected) 20.dp else 0.dp,
                clip = false,
                shape = RoundedCornerShape(Theme.radius.md),
                spotColor = Color(0x00000003)
            )
            .then(
                if (isSelected) Modifier.border(
                    0.5.dp,
                    shape = RoundedCornerShape(Theme.radius.md),
                    color = Theme.colorScheme.stroke
                ) else Modifier
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animatedButtonColor.value)
            .clickable { onSelectChange() }
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(horizontal = Theme.spacing._16, vertical = 9.dp)
    )
}