package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.manage_trends_title
import mena.trends_presentation.generated.resources.profile_image_desc
import mena.trends_presentation.generated.resources.trend_image_desc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.segment.Segment
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.floor

@Composable
fun ManageTrendsScreen(
    viewModel: ManageTrendsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ManageTrendsUiEffect.NavigateBack -> navController.popBackStack()
                is ManageTrendsUiEffect.NavigateToTrend -> navController.navigate(Route.ReelDetails(effect.reelId))
            }
        }
    }

    ManageTrendsScreenContent(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun ManageTrendsScreenContent(
    state: ManageTrendsScreenState,
    listener: ManageTrendsInteractionListener,
) {
    val reels = state.reels.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface),
    ) {
        Spacer(Modifier.height(8.dp))
        AppBar(
            onLeadingClick = { Modifier.clickable { listener.onBackClick() } },
            leadingContent = {
                MenaIcon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back_arrow)
                )
            },
            title = stringResource(Res.string.manage_trends_title),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            AsyncImage(
                model = state.profileImageUrl,
                contentDescription = stringResource(Res.string.profile_image_desc),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
            )
            MenaText(
                text = state.userName,
                style = Theme.typography.label.medium,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )
            SegmentSection(
                reels = reels,
                onTrendClick = listener::onReelItemClick,
            )
        }
    }
}

@Composable
private fun SegmentSection(
    reels: LazyPagingItems<ReelUiState>,
    onTrendClick: (id: String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(Theme.spacing._16)
    ) {
        Segment(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            contentPadding = PaddingValues(top = Theme.spacing._16)
        ) {
            item("My Trends") {
                BoxWithConstraints {
                    val itemWidth = 106.dp
                    val itemHeight = 164.dp
                    val spacing = 4.dp
                    val numColumns =
                        floor((maxWidth.value + spacing.value) / (itemWidth.value + spacing.value)).toInt()
                            .coerceAtLeast(1)
                    val numRows = (reels.itemCount + numColumns - 1) / numColumns
                    val totalHeight =
                        (numRows * itemHeight.value + (numRows - 1) * spacing.value).dp

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(
                            minSize = 106.dp
                        ),
                        modifier = Modifier
                            .height(totalHeight),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        horizontalArrangement = Arrangement.spacedBy(
                            spacing,
                            Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(key = reels.itemKey(), count = reels.itemCount) { index ->
                            reels[index]?.let { reel ->
                                TrendItem(
                                    item = reel,
                                    onTrendClick = onTrendClick
                                )
                            }
                        }

                        if (reels.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) { ///TODO add loading  }
                                }
                            }
                        }
                    }
                }
            }

            item("Favorite") {
                // TODO: Implement favorites, empty for now
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
            .size(106.dp, 164.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrendClick(item.id) }
            .background(Theme.colorScheme.background.surfaceHigh)
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = stringResource(Res.string.trend_image_desc),
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}