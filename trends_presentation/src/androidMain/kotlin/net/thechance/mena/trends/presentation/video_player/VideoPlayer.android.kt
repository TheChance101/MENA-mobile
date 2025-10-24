package net.thechance.mena.trends.presentation.video_player

import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_pause
import mena.trends_presentation.generated.resources.pause_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.video_player.util.Constants.BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.BUFFER_FOR_PLAYBACK_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.MAX_BUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.MIN_BUFFER_MS
import net.thechance.mena.trends.presentation.video_player.util.Constants.SEEK_BAR_DURATION_MS
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    isReelVisible: Boolean,
    modifier: Modifier,
    onVideoPlaying: () -> Unit,
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

    var isLoading by remember { mutableStateOf(true) }
    var isPause by remember { mutableStateOf(false) }

    var currentProgress by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableStateOf(1L) }
    var barWidth by remember { mutableFloatStateOf(1f) }


    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build().apply {
                setSeekParameters(SeekParameters.EXACT)

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isLoading = when (state) {
                            Player.STATE_BUFFERING, Player.STATE_IDLE -> true
                            Player.STATE_READY, Player.STATE_ENDED -> false
                            else -> false
                        }
                    }
                })

                seekTo(lastPosition)
            }
    }

    LaunchedEffect(isReelVisible) {
        if (isReelVisible) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            if (lastPosition > 0) exoPlayer.seekTo(lastPosition)
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        } else {
            lastPosition = exoPlayer.currentPosition
            exoPlayer.pause()
        }

        onVideoPlaying()
    }

    LaunchedEffect(exoPlayer.isPlaying) {
        while (true) {
            duration = exoPlayer.duration.coerceAtLeast(1L)
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

        if (isPause) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pause),
                    contentDescription = stringResource(Res.string.pause_icon)
                )
            }
        }

        if (isLoading && !isPause) {
            Box(
                Modifier.fillMaxSize().background(Theme.colorScheme.brand.brand),
                contentAlignment = Alignment.Center
            ){
                DotsProgressIndicator(
                    colors = listOf(
                        Theme.colorScheme.stroke,
                        Theme.colorScheme.shadeTertiary,
                        Theme.colorScheme.primary.primary
                    )
                )
            }
        }

        ProgressBar(
            progress = { currentProgress },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .padding(bottom = 2.dp, start = 1.dp, end = 1.dp)
                .onGloballyPositioned {
                    barWidth = it.size.width.toFloat()
                }
                .pointerInput(barWidth) {
                    detectTapGestures { offset ->
                        if (duration > 0L && barWidth > 0f) {
                            val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                            val seekPosition = (newProgress * duration).toLong()
                            exoPlayer.seekTo(seekPosition)
                        }
                    }
                },
            trackColor = Theme.colorScheme.primary.onPrimaryHint,
            color = Theme.colorScheme.border.brand
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
                    if (isReelVisible) {
                        if (lastPosition > 0) exoPlayer.seekTo(lastPosition)
                        exoPlayer.play()
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lastPosition = exoPlayer.currentPosition
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}