package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.reel.ReelsPlayerComposable
import chaintech.videoplayer.util.ComposeResourceDrawable
import coil3.compose.rememberAsyncImagePainter
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.delete
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_eye
import mena.trends_presentation.generated.resources.ic_like
import mena.trends_presentation.generated.resources.ic_play
import mena.trends_presentation.generated.resources.react
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.screen.user_reel.components.userReelScreenOverlays
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserReelScreen(
    viewModel: UserReelViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            UserReelEffect.NavigateBack -> navController.popBackStack()
        }
    }

    UserReelScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun UserReelScreenContent(
    state: UserReelState,
    listener: UserReelInteractionListener
) {
    Scaffold(overlays = { userReelScreenOverlays(state, listener) }) {

        val reelPlayerHost = remember {
            MediaPlayerHost(
                mediaUrl = "https://www.youtube.com/shorts/3dx1dBzM7ZY",
                initialVideoFitMode = ScreenResize.FIT,
            )
        }
        val seekBarThumbColor = Theme.colorScheme.primary.onPrimaryHint
        val seekBarActiveTrackColor = Theme.colorScheme.border.brand
        val seekBarInactiveTrackColor = Theme.colorScheme.primary.onPrimaryHint

        val reelPlayerConfig = remember {
            VideoPlayerConfig(
                isPauseResumeEnabled = true,
                isSeekBarVisible = true,
                isDurationVisible = false,
                isSpeedControlEnabled = false,
                isFastForwardBackwardEnabled = false,
                isScreenLockEnabled = false,
                isZoomEnabled = false,
                isMuteControlEnabled = false,
                isGestureVolumeControlEnabled = false,
                isScreenResizeEnabled = false,
                isAutoHideControlEnabled = true,
                isFullScreenEnabled = false,
                seekBarThumbColor = seekBarThumbColor,
                seekBarActiveTrackColor = seekBarActiveTrackColor,
                seekBarInactiveTrackColor = seekBarInactiveTrackColor,
                seekBarBottomPadding = 0.dp,
                seekBarThumbRadius = 8.dp,
                playIconResource = ComposeResourceDrawable(Res.drawable.ic_play),
                pauseResumeIconSize = 48.dp,
            )
        }


        Box(modifier = Modifier.fillMaxSize()) {

            AppBarOptionContainer(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                onClick = listener::onBackClick,
            ) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_arrow_left),
                    contentDescription = "Back Icon",
                )
            }


            ReelsPlayerComposable(
                modifier = Modifier.fillMaxSize(),
                urls = listOf(
                    "https://www.youtube.com/shorts/3dx1dBzM7ZY",
//                    "https://www.youtube.com/shorts/4eVyJFcROUA",
//                    "https://www.youtube.com/shorts/_V9ClifEIcE",
//                    "https://www.youtube.com/shorts/XkYJm1SzeG8",
//                    "https://www.youtube.com/shorts/q0eHKMOhx-4"
                ),
                playerConfig = reelPlayerConfig,
            )

            UsersReAct(
                viewCount = state.viewsCount.toString(),
                likeCount = state.likesCount.toString(),
                onDeleteClick = listener::onDeleteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Theme.spacing._16, bottom = 140.dp)
            )

            PublisherDetails(
                userName = state.username,
                timeOfPublish = state.createdAt,
                description = state.description,
                avatar = state.thumbnail,
                modifier = Modifier.align(Alignment.BottomCenter),
                isDescriptionExpanded = state.isDescriptionExpanded,
                onDescriptionClick = listener::onDescriptionClick
            )
        }
    }
}

@Composable
private fun PublisherDetails(
    avatar: String,
    userName: String,
    timeOfPublish: String,
    isDescriptionExpanded: Boolean,
    onDescriptionClick: (Boolean) -> Unit,
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
            modifier = Modifier
                .padding(top = Theme.spacing._8)
        ) {

            Image(
                painter = rememberAsyncImagePainter(avatar),
                modifier = Modifier.size(size = 40.dp).clip(shape = CircleShape)
                    .border(shape = CircleShape, width = 0.5.dp, color = Theme.colorScheme.stroke),
                contentScale = ContentScale.Crop,
                contentDescription = "avatar image"
            )

            Column(Modifier.padding(bottom = Theme.spacing._16)) {
                Text(
                    text = userName,
                    color = Theme.colorScheme.primary.onPrimary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(vertical = Theme.spacing._2)
                )

                Text(
                    text = timeOfPublish,
                    color = Theme.colorScheme.shadeTertiary,
                    style = Theme.typography.label.small
                )
            }
        }

        if (description.isNotBlank()) {
            Text(
                text = description,
                modifier = Modifier
                    .animateContentSize()
                    .clickable { onDescriptionClick(isDescriptionExpanded) },
                color = Theme.colorScheme.primary.onPrimary,
                style = Theme.typography.label.medium,
                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 1
            )
        }
    }
}

@Composable
private fun UsersReAct(
    likeCount: String,
    viewCount: String,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._24)
    ) {
        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_like),
            label = likeCount
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_eye),
            label = viewCount
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_delete),
            label = stringResource(Res.string.delete),
            onClick = { onDeleteClick() },

            )
    }
}

@Composable
private fun ReActIcon(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = stringResource(Res.string.react),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { onClick() },
            tint = Theme.colorScheme.shadeTertiary
        )
        Text(
            text = label,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeTertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        UserReelScreen()
    }
}