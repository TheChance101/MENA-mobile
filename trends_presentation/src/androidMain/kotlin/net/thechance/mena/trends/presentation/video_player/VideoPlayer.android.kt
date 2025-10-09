package net.thechance.mena.trends.presentation.video_player


import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView



@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    playWhenVisible: Boolean,
    autoPlay: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastPosition by rememberSaveable(url) { mutableStateOf(0L) }


    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
            seekTo(lastPosition)
        }
    }

    LaunchedEffect(playWhenVisible) {
        if (playWhenVisible) {
            exoPlayer.play()
        } else {
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
                    if (playWhenVisible && autoPlay) {
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
                    useController = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowRewindButton(false)
                    setShowFastForwardButton(false)
                    setShowSubtitleButton(false)
                    setShowVrButton(false)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}