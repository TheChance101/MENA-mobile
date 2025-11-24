package net.thechance.mena.trends.presentation.video_player

import android.view.View
import androidx.annotation.OptIn
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.di.trendStorageAccessSecret
import net.thechance.mena.trends.presentation.screen.user_trend.TrendWatchSessionState
import net.thechance.mena.trends.presentation.video_player.composable.LoadingItem
import net.thechance.mena.trends.presentation.video_player.composable.PauseIcon
import net.thechance.mena.trends.presentation.video_player.util.Constants.BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.BUFFER_FOR_PLAYBACK_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.MAX_BUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.MIN_BUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.SEEK_BAR_DURATION_MS
import net.thechance.mena.trends.presentation.video_player.utils.getCurrentTime
import java.net.UnknownHostException

private const val HTTP_UNAUTHORIZED_STATUS_EXCEPTION = 403

@OptIn(UnstableApi::class)
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
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastPosition by rememberSaveable(url) { mutableLongStateOf(0L) }

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            MIN_BUFFER_MS,
            MAX_BUFFER_MS,
            BUFFER_FOR_PLAYBACK_MS,
            BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        )
        .build()

    val source = remember {
        DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("X-ACCESS-KEY" to trendStorageAccessSecret))
    }


    val mediaItem = remember(url) {
        MediaItem.Builder()
            .setUri(url)
            .setCustomCacheKey(cacheKey)
            .build()
    }

    var isLoading by remember { mutableStateOf(true) }
    var isPause by remember { mutableStateOf(false) }

    var currentProgress by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(1) }
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

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(source)
            )
            .build().apply {
                setSeekParameters(SeekParameters.EXACT)
                repeatMode = Player.REPEAT_MODE_ONE

                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        val cause = error.cause

                        when {
                            cause is UnknownHostException ||
                                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                                onNetworkError()
                            }

                            cause is HttpDataSource.InvalidResponseCodeException &&
                                    cause.responseCode == HTTP_UNAUTHORIZED_STATUS_EXCEPTION -> {
                                onRequestRefresh()
                            }

                            else -> super.onPlayerError(error)
                        }
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        isLoading = when (state) {
                            Player.STATE_IDLE -> true
                            Player.STATE_READY -> {
                                isStartPlaying = true
                                false
                            }

                            Player.STATE_BUFFERING -> {
                                isInitialBuffering = !isStartPlaying
                                true
                            }

                            Player.STATE_ENDED -> {
                                if (!isWatchedToEnd.value) isWatchedToEnd.value = true
                                false
                            }

                            else -> false
                        }
                    }
                })

                seekTo(lastPosition)
            }
    }

    LaunchedEffect(url) {
        val savedPosition = exoPlayer.currentPosition
        exoPlayer.setMediaItem(mediaItem, false)
        exoPlayer.prepare()
        exoPlayer.seekTo(savedPosition)
        if (isTrendVisible) {
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        }
    }

    LaunchedEffect(isTrendVisible) {
        if (isTrendVisible) {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            if (lastPosition > 0) exoPlayer.seekTo(lastPosition)
            exoPlayer.playWhenReady = true
            exoPlayer.play()
            trendWatchSessionState.watchStartTime = getCurrentTime()
        } else {
            lastPosition = exoPlayer.currentPosition
            exoPlayer.pause()
            trendWatchSessionState.watchEndTime = getCurrentTime()
        }

        onVideoPlaying()
    }

    LaunchedEffect(exoPlayer.isPlaying) {
        while (true) {
            duration = exoPlayer.duration.coerceAtLeast(1L)
            trendWatchSessionState.videoDurationInMilliseconds = duration
            val position = exoPlayer.currentPosition
            currentProgress = position.toFloat() / duration.toFloat()
            delay(SEEK_BAR_DURATION_MS)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false

                    post {
                        findViewById<View>(
                            androidx.media3.ui.R.id.exo_settings
                        ).visibility = View.GONE
                    }
                }
            },
            update = { playerView ->
                if (isPause) exoPlayer.pause()
                else exoPlayer.play()
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPause = !isPause }
        )

        if (isPause)
            PauseIcon(modifier = Modifier.align(Alignment.Center))

        if (isLoading && !isPause)
            LoadingItem(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor.value)
            )

        ProgressBar(
            progress = { currentProgress },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .padding(bottom = 2.dp)
                .height(barHeight)
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
                            if (duration > 0L && barWidth > 0f) {
                                val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                                val seekPosition = (newProgress * duration).toLong()
                                exoPlayer.seekTo(seekPosition)
                            }
                        }
                    )
                },
            trackColor = Theme.colorScheme.primary.onPrimaryHint,
            color = Theme.colorScheme.border.brand,
        )
        content()
    }

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> {
                    lastPosition = exoPlayer.currentPosition
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isTrendVisible) {
                        if (lastPosition > 0) exoPlayer.seekTo(lastPosition)
                        if (!isPause) exoPlayer.play()
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lastPosition = exoPlayer.currentPosition
            exoPlayer.pause()
            if (!isWatchedToEnd.value) trendWatchSessionState.watchedDurationInMilliseconds =
                lastPosition
            trendWatchSessionState.watchEndTime = getCurrentTime()
            saveTrendWatchSession(trendWatchSessionState)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}