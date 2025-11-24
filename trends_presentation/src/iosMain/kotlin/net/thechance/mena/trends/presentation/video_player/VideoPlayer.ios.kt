package net.thechance.mena.trends.presentation.video_player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_pause
import mena.trends_presentation.generated.resources.pause_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.di.trendStorageAccessSecret
import net.thechance.mena.trends.presentation.screen.user_trend.TrendWatchSessionState
import net.thechance.mena.trends.presentation.video_player.composable.LoadingItem
import net.thechance.mena.trends.presentation.video_player.utils.getCurrentTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerItemStatusUnknown
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.actionAtItemEnd
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.Foundation.NSURLErrorBadServerResponse

private const val PREFERRED_TIME_SCALE = 600
private const val NSURLErrorNotConnectedToInternet = -1009

private const val DELAY_TIME = 250L

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    isTrendVisible: Boolean,
    modifier: Modifier,
    cacheKey: String?,
    onVideoPlaying: () -> Unit,
    onRequestRefresh: () -> Unit,
    onNetworkError: () -> Unit,
    saveTrendWatchSession: (TrendWatchSessionState) -> Unit,
    content: @Composable (() -> Unit)
) {
    var lastPosition by rememberSaveable(url) { mutableStateOf(0.0) }
    var isPaused by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var currentProgress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1.0) }
    var barWidth by remember { mutableFloatStateOf(1f) }
    var isPressed by remember { mutableStateOf(false) }
    val barHeight by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 4.dp,
    )

    var isStartPlaying by remember { mutableStateOf(false) }
    var isInitialBuffering by remember { mutableStateOf(true) }

    val backgroundColor = animateColorAsState(
        targetValue = if (isInitialBuffering) Theme.colorScheme.brand.brand
        else Color.Transparent,
    )

    val trendWatchSessionState = remember(url) { TrendWatchSessionState() }
    val isWatchedToEnd = remember { mutableStateOf(false) }

    val headers = mapOf("X-ACCESS-KEY" to trendStorageAccessSecret)

    val asset = remember(url) {
        AVURLAsset.URLAssetWithURL(
            URL = NSURL(string = url),
            options = mapOf("AVURLAssetHTTPHeaderFieldsKey" to headers)
        )
    }
    val avPlayerItem = remember(url) { AVPlayerItem(asset) }

    val player = remember(url) { AVPlayer(avPlayerItem) }

    player.actionAtItemEnd = 1

    val playerView = remember(url) { PlayerLayerView(player) }

    LaunchedEffect(url) {

        val currentTime = CMTimeGetSeconds(player.currentTime())
        val wasPlaying = player.rate > 0.0f

        val newAsset = AVURLAsset.URLAssetWithURL(
            URL = NSURL(string = url),
            options = mapOf("AVURLAssetHTTPHeaderFieldsKey" to headers)
        )
        val newPlayerItem = AVPlayerItem(newAsset)

        player.replaceCurrentItemWithPlayerItem(newPlayerItem)

        val time = CMTimeMakeWithSeconds(currentTime, PREFERRED_TIME_SCALE)
        player.seekToTime(time)

        if (wasPlaying) {
            player.play()
        }
    }

    LaunchedEffect(player) {
        while (true) {
            val item = player.currentItem
            if (item?.status == AVPlayerItemStatusFailed) {
                val error = item.error
                if (error != null) {
                    when {
                        error.domain == "NSURLErrorDomain" &&
                                error.code.toInt() == NSURLErrorNotConnectedToInternet -> {
                            onNetworkError()
                            return@LaunchedEffect
                        }

                        error.code == NSURLErrorBadServerResponse -> {
                            onRequestRefresh()
                            return@LaunchedEffect
                        }
                    }
                }
            }
            delay(DELAY_TIME)
        }
    }

    LaunchedEffect(url, isTrendVisible) {
        replayReelWhenFinishedAutomatic(player) { isWatchedToEnd.value = true }

        if (isTrendVisible) {
            if (lastPosition > 0.0) {
                val time = CMTimeMakeWithSeconds(lastPosition, PREFERRED_TIME_SCALE)
                player.seekToTime(time)
            }
            player.play()
            isPaused = false
            trendWatchSessionState.watchStartTime = getCurrentTime()
        } else {
            lastPosition = CMTimeGetSeconds(player.currentTime())
            player.pause()
            isPaused = true
            trendWatchSessionState.watchEndTime = getCurrentTime()
        }

        onVideoPlaying()
    }

    LaunchedEffect(player) {
        while (true) {
            val item = player.currentItem
            val itemReady = when (item?.status) {
                AVPlayerItemStatusReadyToPlay -> {
                    isStartPlaying = true
                    true
                }

                AVPlayerItemStatusFailed -> true
                AVPlayerItemStatusUnknown, null -> false
                else -> false
            }

            val waiting =
                player.timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate

            isLoading = (!itemReady) || waiting

            if (waiting) isInitialBuffering = !isStartPlaying

            delay(DELAY_TIME)
        }
    }

    LaunchedEffect(player) {
        while (true) {
            val currentItem = player.currentItem
            if (currentItem != null) {
                val totalSeconds = CMTimeGetSeconds(currentItem.duration)
                trendWatchSessionState.videoDurationInMilliseconds = duration.toLong()
                if (!totalSeconds.isNaN() && totalSeconds > 0.0) {
                    duration = totalSeconds
                    val currentSeconds = CMTimeGetSeconds(player.currentTime())
                    currentProgress = if (!currentSeconds.isNaN()) {
                        (currentSeconds / totalSeconds).toFloat().coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                }
            }
            delay(DELAY_TIME)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        UIKitView(
            factory = { playerView },
            update = { view ->
                view.setNeedsLayout()
                view.layoutIfNeeded()
                if (isPaused) player.pause() else player.play()
            },
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    isPaused = !isPaused
                    if (isPaused) player.pause() else player.play()
                }
        ) {
            content()

            if (isPaused) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pause),
                    contentDescription = stringResource(Res.string.pause_icon),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (isLoading && !isPaused)
                LoadingItem(modifier = Modifier.fillMaxSize().background(backgroundColor.value))

            ProgressBar(
                progress = { currentProgress },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .height(barHeight)
                    .padding(bottom = 2.dp)
                    .onGloballyPositioned {
                        barWidth = it.size.width.toFloat()
                    }
                    .pointerInput(barWidth, duration) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                awaitRelease()
                                isPressed = false
                            },
                            onTap = { offset ->
                                if (duration > 0.0 && barWidth > 0f) {
                                    val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                                    val seekSeconds = newProgress * duration
                                    val seekTime =
                                        CMTimeMakeWithSeconds(seekSeconds, PREFERRED_TIME_SCALE)
                                    player.seekToTime(seekTime)
                                }
                            }
                        )
                    },
                trackColor = Theme.colorScheme.primary.onPrimaryHint,
                color = Theme.colorScheme.border.brand
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            lastPosition = CMTimeGetSeconds(player.currentTime())
            if (!isWatchedToEnd.value) trendWatchSessionState.watchedDurationInMilliseconds =
                lastPosition.toLong()
            trendWatchSessionState.watchEndTime = getCurrentTime()
            saveTrendWatchSession(trendWatchSessionState)
            player.pause()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun replayReelWhenFinishedAutomatic(player: AVPlayer, onVideoEnded: () -> Unit) {
    NSNotificationCenter.defaultCenter.addObserverForName(
        name = AVPlayerItemDidPlayToEndTimeNotification,
        `object` = player.currentItem,
        queue = null
    ) { _ ->
        onVideoEnded()
        player.seekToTime(CMTimeMakeWithSeconds(0.0, PREFERRED_TIME_SCALE))
        player.play()
    }
}