package net.thechance.mena.trends.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.home.component.FeedReelCard
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
                navController.navigate(Route.ReelDetails(effect.trendId))

            is HomeUiEffect.NavigateToAddReel ->
                navController.navigate(Route.UploadReel)

            is HomeUiEffect.NavigateToChangeTags ->
                navController.navigate(Route.UpdateCategories)

            is HomeUiEffect.NavigateToManageMyTrends ->
                navController.navigate(Route.ManageReels)
        }
    }

    LaunchedEffect(Unit) { viewModel.getFeedReels() }

    ReelScreenContent(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun ReelScreenContent(
    state: HomeScreenState,
    listener: HomeInteractionListener,
) {
    Scaffold(
        topBar = {
            TrendsAppBar(
                onManageMyTrendsClick = listener::onClickManageMyTrends,
                onEditTagsClick = listener::onClickEditTags
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val reels = state.reels.collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Theme.spacing._16),
                contentPadding = PaddingValues(vertical = Theme.spacing._8),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._16)
            ) {
                items(reels.itemSnapshotList.items) { reel ->
                    FeedReelCard(
                        reel = reel,
                        onLikeClick = { listener.onClickLike(reel.id) },
                        onReelClick = { listener.onClickReel(reel.id) }
                    )
                }
            }

            Icon(
                painter = painterResource(Res.drawable.ic_add_real),
                contentDescription = stringResource(Res.string.add_reel),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Theme.spacing._16, bottom = Theme.spacing._16)
                    .size(56.dp)
                    .clip(RoundedCornerShape(Theme.radius.lg))
                    .background(Theme.colorScheme.primary.primary)
                    .noRippleClickable { listener.onClickAddReel() }
                    .padding(Theme.spacing._16),
            )
        }
    }
}

@Composable
private fun TrendsAppBar(
    onManageMyTrendsClick: () -> Unit,
    onEditTagsClick: () -> Unit
) {
    AppBar(
        title = stringResource(Res.string.trends_title),
        trailingContent = {
            AppBarOptionContainer(
                isBadgeVisible = false,
                onClick = onManageMyTrendsClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_account_setting),
                    contentDescription = stringResource(Res.string.manage_trends),
                    tint = Theme.colorScheme.shadePrimary
                )
            }
            AppBarOptionContainer(
                isBadgeVisible = false,
                onClick = onEditTagsClick
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