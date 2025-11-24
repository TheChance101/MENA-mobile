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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.favorite
import mena.trends_presentation.generated.resources.ic_empty_trends
import mena.trends_presentation.generated.resources.ic_empty_trends_dark
import mena.trends_presentation.generated.resources.ic_paly_now
import mena.trends_presentation.generated.resources.ic_placeholder_profile
import mena.trends_presentation.generated.resources.manage_trends_title
import mena.trends_presentation.generated.resources.my_trends
import mena.trends_presentation.generated.resources.no_favorites_description
import mena.trends_presentation.generated.resources.no_favorites_title
import mena.trends_presentation.generated.resources.play_now
import mena.trends_presentation.generated.resources.profile_image_desc
import mena.trends_presentation.generated.resources.trend_image_desc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalDarkTheme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.home.component.EmptyTrends
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.base.toErrorState
import net.thechance.mena.trends.presentation.shared.component.BackIcon
import net.thechance.mena.trends.presentation.shared.component.BaseAsyncImage
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.StatePlaceholder
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
                navController.navigate(
                    Route.TrendDetails(trendId = effect.trendId, source = effect.trendSource.name)
                )
            }
        }
    }

    LaunchedEffect(state.selectedTab) {
        viewModel.loadSelectedTabData(state.selectedTab)
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
            val trends = when (state.selectedTab) {
                SelectTab.MyTrends -> state.trends
                SelectTab.Favorites -> state.favoriteTrends
            }.collectAsLazyPagingItems()

            TrendsAnimatedVisibility(
                visible = trends.loadState.refresh is LoadState.Loading,
                content = { LoadingProgressBar() }
            )

            TrendsAnimatedVisibility(
                visible = state.error == ErrorState.NoInternet,
                content = { NoConnection { listener.onClickRetry() } }
            )

            TrendsAnimatedVisibility(
                visible = state.error == null && state.isLoading.not(),
                content = { ManageTrendsScreenBody(listener, state, trends) }
            )
        }
    )
}

@Composable
private fun ManageTrendsScreenBody(
    listener: ManageTrendsInteractionListener,
    state: ManageTrendsScreenState,
    trends: LazyPagingItems<TrendUiState>
) {
    val cardWidth = 106.dp
    val gridState = rememberLazyGridState()
    val shouldShowEmptyState = trends.itemSnapshotList.isEmpty() &&
            trends.loadState.refresh is LoadState.NotLoading &&
            trends.loadState.refresh.toErrorState() == null

    LazyVerticalGrid(
        state = gridState,
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
                    .padding(top = Theme.spacing._32, bottom = Theme.spacing._8)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = state.profile.userName,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
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
                    isSelected = state.selectedTab == SelectTab.MyTrends,
                    onSelectChange = { listener.onSelectTab(SelectTab.MyTrends) },
                    modifier = Modifier.weight(1f)
                )

                SegmentButton(
                    title = stringResource(Res.string.favorite),
                    isSelected = state.selectedTab == SelectTab.Favorites,
                    onSelectChange = { listener.onSelectTab(SelectTab.Favorites) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (trends.itemSnapshotList.isNotEmpty()) {
            items(key = trends.itemKey(), count = trends.itemCount) { index ->

                trends[index]?.let { trend ->
                    TrendItem(
                        item = trend,
                        onTrendClick = listener::onClickTrend,
                        onGetRefreshedThumbnail = listener::onGetRefreshedThumbnail
                    )
                }
            }
        } else if (shouldShowEmptyState) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyStateForTab(tab = state.selectedTab)
            }
        }
    }
}

@Composable
private fun EmptyStateForTab(tab: SelectTab) {
    if (tab == SelectTab.Favorites) {
        EmptyFavorites(modifier = Modifier.padding(top = 74.dp))
    } else {
        EmptyTrends(isScrollable = false, modifier = Modifier.padding(top = 74.dp))
    }
}

@Composable
private fun EmptyFavorites(modifier: Modifier = Modifier) {
    val icon = if (LocalDarkTheme.current)
        painterResource(Res.drawable.ic_empty_trends_dark)
    else
        painterResource(Res.drawable.ic_empty_trends)

    StatePlaceholder(
        icon = icon,
        title = stringResource(Res.string.no_favorites_title),
        description = stringResource(Res.string.no_favorites_description),
        isScrollable = false,
        modifier = modifier
    )
}

@Composable
private fun ManageMyTrendsAppBar(onBackClick: () -> Unit) {
    AppBar(
        onLeadingClick = onBackClick,
        title = stringResource(Res.string.manage_trends_title),
        leadingContent = {
            BackIcon()
        }
    )
}

@Composable
private fun UserAvatar(
    profileImageUrl: String,
    modifier: Modifier = Modifier
) {
    val defaultPainter = painterResource(Res.drawable.ic_placeholder_profile)
    val tintColor = Theme.colorScheme.shadePrimary
    val hasImage = profileImageUrl.isNotBlank()
    val tintedErrorPainter = remember(defaultPainter) {
        object : Painter() {
            override val intrinsicSize = defaultPainter.intrinsicSize

            override fun DrawScope.onDraw() {
                with(defaultPainter) {
                    draw(
                        size = size,
                        colorFilter = ColorFilter.tint(tintColor)
                    )
                }
            }
        }
    }

    if (hasImage.not()) {
        EmptyProfilePicture(defaultPainter = defaultPainter)
    } else {
        AsyncImage(
            model = profileImageUrl,
            contentDescription = stringResource(Res.string.profile_image_desc),
            error = tintedErrorPainter,
            modifier = modifier.size(100.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun EmptyProfilePicture(defaultPainter: Painter) {
    val isDarkTheme = LocalDarkTheme.current
    val backgroundColor = if (isDarkTheme) Theme.colorScheme.stroke else Color.White
    val iconTint = if (isDarkTheme) Color.White else Color.Black

    Icon(
        painter = defaultPainter,
        contentDescription = stringResource(Res.string.profile_image_desc),
        tint = iconTint,
        modifier = Modifier
            .requiredSize(88.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = iconTint.copy(alpha = 0.70f),
                spotColor = iconTint.copy(alpha = 0.70f)
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(16.dp),
    )
}

@Composable
private fun TrendItem(
    item: TrendUiState,
    onTrendClick: (id: String) -> Unit,
    onGetRefreshedThumbnail: (trendId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidthRatio = 106f / 164f
    val thumbnailUrl = remember(item.id) { item.thumbnailUrl }

    Box(
        modifier = modifier
            .aspectRatio(cardWidthRatio)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrendClick(item.id) }
            .background(color = Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center

    ) {
        TrendsAnimatedVisibility(visible = thumbnailUrl.isNotBlank()) {
            BaseAsyncImage(
                url = thumbnailUrl,
                contentDescription = stringResource(Res.string.trend_image_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onRequestRefresh = { onGetRefreshedThumbnail(item.id) },
                imageCacheKey = item.id,
            )
        }

        TrendsAnimatedVisibility(visible = thumbnailUrl.isBlank()) {
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