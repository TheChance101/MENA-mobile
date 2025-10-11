package net.thechance.mena.trends.presentation.video_player


import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    playWhenVisible: Boolean,
    onControllerVisibilityChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastPosition by rememberSaveable(url) { mutableLongStateOf(0L) }

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(200, 5000, 100, 200)
        .build()

    var isLoading by remember { mutableStateOf(true) }


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

    LaunchedEffect(playWhenVisible) {
        if (playWhenVisible) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            if (lastPosition > 0) exoPlayer.seekTo(lastPosition)
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        } else {
            lastPosition = exoPlayer.currentPosition
            exoPlayer.pause()
        }
    }

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> {
                    lastPosition = exoPlayer.currentPosition
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (playWhenVisible) {
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


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    controllerShowTimeoutMs = 0
                    controllerAutoShow = false

                    setControllerVisibilityListener(
                        PlayerView.ControllerVisibilityListener { visibility ->
                            val visible = visibility == View.VISIBLE
                            onControllerVisibilityChanged(visible)
                        }
                    )

                    post {
                        findViewById<View>(
                            androidx.media3.ui.R.id.exo_settings
                        ).visibility = View.GONE
                    }

                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowRewindButton(false)
                    setShowFastForwardButton(false)
                    setShowSubtitleButton(false)
                    setShowVrButton(false)
                }
            },
            update = { playerView ->
                playerView.useController = !isLoading
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            DotsProgressIndicator(
                colors = listOf(
                    Theme.colorScheme.stroke,
                    Theme.colorScheme.shadeTertiary,
                    Theme.colorScheme.primary.primary
                )
            )
        }
    }
}