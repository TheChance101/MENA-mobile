package net.thechance.mena.trends.presentation.screen.user_trend

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.avatar_image
import mena.trends_presentation.generated.resources.confirmation_message
import mena.trends_presentation.generated.resources.delete
import mena.trends_presentation.generated.resources.delete_trend
import mena.trends_presentation.generated.resources.fail_delete_message
import mena.trends_presentation.generated.resources.fail_delete_title
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_eye
import mena.trends_presentation.generated.resources.ic_like
import mena.trends_presentation.generated.resources.just_now
import mena.trends_presentation.generated.resources.react
import mena.trends_presentation.generated.resources.success_delete_message
import mena.trends_presentation.generated.resources.success_delete_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.BackIcon
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.shared.util.asString
import net.thechance.mena.trends.presentation.shared.util.gradientShadow
import net.thechance.mena.trends.presentation.video_player.VideoPlayer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserTrendScreen(
    viewModel: UserTrendViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            UserTrendEffect.NavigateBack -> navController.popBackStack()
            UserTrendEffect.NavigateToPublisherProfile -> navController.navigate(Route.ManageTrends)
        }
    }

    UserTrendScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun UserTrendScreenContent(
    state: UserTrendState,
    listener: UserTrendInteractionListener
) {
    Scaffold(
        overlays = {
            dialog(isVisible = state.isConfirmationDialogVisible) {
                Dialog(
                    title = stringResource(Res.string.delete_trend),
                    message = stringResource(Res.string.confirmation_message),
                    isVisible = state.isConfirmationDialogVisible,
                    onDismiss = { listener.onDismissConfirmationDialog() },
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onCancelClick = { listener.onDismissConfirmationDialog() },
                    dialogCornerShape = RoundedCornerShape(Theme.radius.md),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(Theme.spacing._16),
                    actionButtons = {
                        Text(
                            text = stringResource(Res.string.delete),
                            color = Theme.colorScheme.error,
                            style = Theme.typography.label.medium,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(
                                    top = Theme.spacing._24,
                                    bottom = Theme.spacing._12,
                                    end = Theme.spacing._8
                                )
                                .clickable(
                                    onClick = { listener.onClickConfirmDelete() },
                                    role = Role.Button,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                        )
                    }
                )
            }

            dialog(state.isTrendDeleted == true) {
                Dialog(
                    title = stringResource(Res.string.success_delete_title),
                    message = stringResource(Res.string.success_delete_message),
                    isVisible = state.isTrendDeleted == true,
                    onDismiss = {
                        listener.onDismissSuccessDialog()
                        listener.onClickBack()
                    },
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onCancelClick = {
                        listener.onDismissSuccessDialog()
                        listener.onClickBack()
                    },
                    dialogCornerShape = RoundedCornerShape(Theme.radius.md),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(Theme.spacing._16),
                    actionButtons = {}
                )
            }

            dialog(state.isTrendDeleted == false) {
                Dialog(
                    title = stringResource(Res.string.fail_delete_title),
                    message = stringResource(Res.string.fail_delete_message),
                    isVisible = state.isTrendDeleted == false,
                    onDismiss = { listener.onDismissErrorDialog() },
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onCancelClick = { listener.onDismissErrorDialog() },
                    dialogCornerShape = RoundedCornerShape(Theme.radius.md),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(Theme.spacing._16),
                    actionButtons = {}
                )
            }
        }
    ) {

        val trends = state.trends.collectAsLazyPagingItems()
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { trends.itemCount },
        )

        LaunchedEffect(pagerState.currentPage) {
            if (trends.itemCount > 0) {
                trends[pagerState.currentPage]?.let { trend ->
                    listener.onChangeCurrentTrend(trend.id)
                }
            }
        }

        TopAppBar(onBackClick = listener::onClickBack, modifier = Modifier.zIndex(5f))

        val hasNetworkError = state.error is ErrorState.NoInternet

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
                .background(if (hasNetworkError) Theme.colorScheme.background.surface else Color.Black),
            key = { page -> trends[page]?.id ?: page },
        ) { page ->

            trends[page]?.let { trend ->
                if (hasNetworkError) {
                    NoConnection(onRetry = { listener.onClickRetry(trend.id) })
                } else
                    TrendContent(
                        trend = trend,
                        shouldRender = (pagerState.currentPage == page),
                        isDescriptionExpanded = state.isDescriptionExpanded,
                        onDeleteClick = listener::onClickDelete,
                        onDescriptionClick = listener::onClickDescription,
                        onPublisherInfoClick = listener::onClickPublisherInfo,
                        incrementViewsCount = { listener.increaseTrendView(trend.id) },
                        onLikeClick = { listener.onClickLike(trend.id, trend.isLiked) },
                        onGetRefreshUrl = listener::onGetRefreshVideoUrl,
                        saveTrendWatchSession = { listener.saveUserTrendEngagement(it, trend.id) },
                        onNetworkError = listener::onNetworkError
                    )
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        title = "",
        modifier = modifier
            .fillMaxWidth()
            .gradientShadow()
            .padding(horizontal = Theme.spacing._16)
            .padding(top = Theme.spacing._8),
        contentPadding = PaddingValues(0.dp),
        leadingContent = {
            BackIcon()
        },
        onLeadingClick = { onBackClick() }
    )
}

@Composable
private fun TrendContent(
    trend: UserTrendUiState,
    shouldRender: Boolean,
    isDescriptionExpanded: Boolean,
    onDeleteClick: () -> Unit,
    onDescriptionClick: (isCollapsed: Boolean) -> Unit,
    onPublisherInfoClick: () -> Unit,
    incrementViewsCount: () -> Unit,
    onGetRefreshUrl: (trendId: String) -> Unit,
    onLikeClick: () -> Unit,
    saveTrendWatchSession: (TrendWatchSessionState) -> Unit,
    onNetworkError: () -> Unit
) {
    val rememberedUrl = remember(trend.id) { trend.videoUrl }
    VideoPlayer(
        modifier = Modifier.background(Color.Black),
        url = rememberedUrl,
        isTrendVisible = shouldRender,
        onVideoPlaying = incrementViewsCount,
        cacheKey = trend.id,
        onRequestRefresh = { onGetRefreshUrl(trend.id) },
        saveTrendWatchSession = saveTrendWatchSession,
        onNetworkError = onNetworkError
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {

            val isLandscape = maxWidth > maxHeight

            UsersReact(
                viewCount = trend.viewsCount.toString(),
                likeCount = trend.likesCount.toString(),
                isCurrentUserOwner = trend.isCurrentUserOwner,
                onDeleteClick = onDeleteClick,
                onLikeClick = onLikeClick,
                isLiked = trend.isLiked,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Theme.spacing._16,
                        bottom = if (isLandscape) 60.dp else 140.dp
                    )
            )

            PublisherInfo(
                userName = trend.username,
                timeOfPublish = trend.createdAt?.asString()
                    ?: stringResource(resource = Res.string.just_now),
                description = trend.description,
                avatar = trend.profileImageUrl,
                modifier = Modifier.align(Alignment.BottomCenter),
                isDescriptionExpanded = isDescriptionExpanded,
                onDescriptionClick = onDescriptionClick,
                onPublisherInfoClick = onPublisherInfoClick.takeIf { trend.isCurrentUserOwner } ?: {}
            )

            Box(modifier = Modifier.fillMaxWidth().height(height = 118.dp).gradientShadow())
        }
    }
}

@Composable
private fun PublisherInfo(
    avatar: String,
    userName: String,
    timeOfPublish: String,
    isDescriptionExpanded: Boolean,
    onPublisherInfoClick: () -> Unit,
    onDescriptionClick: (isCollapsed: Boolean) -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Theme.spacing._32)
            .padding(horizontal = Theme.spacing._16)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._8),
            modifier = Modifier.padding(top = Theme.spacing._8)
        ) {

            Image(
                painter = rememberAsyncImagePainter(avatar),
                modifier = Modifier.size(size = 40.dp).clip(shape = CircleShape)
                    .border(shape = CircleShape, width = 0.5.dp, color = Theme.colorScheme.stroke)
                    .clickable { onPublisherInfoClick() },
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(Res.string.avatar_image)
            )

            Column(Modifier.padding(bottom = Theme.spacing._16)) {
                Text(
                    text = userName,
                    color = Color.White,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(vertical = Theme.spacing._2)
                        .clickable { onPublisherInfoClick() },
                )

                Text(
                    text = timeOfPublish,
                    color = Theme.colorScheme.shadeTertiary,
                    style = Theme.typography.label.small
                )
            }
        }

        TrendsAnimatedVisibility(visible = description.isNotBlank()) {
            Text(
                text = description,
                modifier = Modifier
                    .animateContentSize()
                    .clickable { onDescriptionClick(isDescriptionExpanded) },
                color = Color.White,
                style = Theme.typography.label.medium,
                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 1
            )
        }
    }
}

@Composable
private fun UsersReact(
    likeCount: String,
    viewCount: String,
    isCurrentUserOwner: Boolean,
    isLiked: Boolean,
    onDeleteClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._24)
    ) {

        val likeIconColor by animateColorAsState(
            targetValue = if (isLiked) Color.White else Theme.colorScheme.shadeTertiary,
            animationSpec = tween(durationMillis = 500)
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_like),
            onClick = onLikeClick,
            tint = likeIconColor,
            label = likeCount
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_eye),
            isClickEnabled = false,
            label = viewCount
        )

        TrendsAnimatedVisibility(visible = isCurrentUserOwner) {
            ReActIcon(
                icon = painterResource(resource = Res.drawable.ic_delete),
                label = stringResource(Res.string.delete),
                onClick = { onDeleteClick() },
            )
        }
    }
}

@Composable
private fun ReActIcon(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
    isClickEnabled: Boolean = true,
    tint: Color = Theme.colorScheme.shadeTertiary,
    onClick: () -> Unit = {}
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = stringResource(Res.string.react),
            modifier = Modifier
                .padding(bottom = Theme.spacing._8)
                .scale(scale.value)
                .noRippleClickable(enabled = isClickEnabled) {
                    onClick()
                    scope.launch {
                        scale.animateTo(1.4f, tween(200))
                        scale.animateTo(1f, tween(200))
                    }
                },
            tint = tint
        )
        Text(
            text = label,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeTertiary,
            textAlign = TextAlign.Center
        )
    }
}