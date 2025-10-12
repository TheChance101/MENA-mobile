package net.thechance.mena.trends.presentation.video_player


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.asset
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.darwin.Float64

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    playWhenVisible: Boolean,
) {

    val player = remember { AVPlayer(uRL = NSURL.URLWithString(url)!!) }
    val avPlayerViewController = remember { AVPlayerViewController() }
    avPlayerViewController.player = player

    var isLoading by remember { mutableStateOf(true) }
    var isPause by remember { mutableStateOf(false) }

    var currentProgress by remember { mutableStateOf(0f) }
    var duration: Float64 by remember { mutableStateOf(1.0) }
    var barWidth by remember { mutableFloatStateOf(1f) }

    var lastPosition by remember { mutableDoubleStateOf(0.0) }


    // Handle visibility (play/pause)
    LaunchedEffect(playWhenVisible) {
        if (playWhenVisible) {
            if (lastPosition > 0) {
                player.seekToTime(CMTimeMakeWithSeconds(lastPosition, preferredTimescale = 1))
            }
            player.play()
        } else {
            lastPosition = CMTimeGetSeconds(player.currentTime())
            player.pause()
        }
    }

    // Update Progress bar value
    LaunchedEffect(Unit) {
        val item = player.currentItem
        if (item != null) {
            duration = CMTimeGetSeconds(item.asset.duration)
            isLoading = false
        }

        while (true) {
            val current = CMTimeGetSeconds(player.currentTime())
            currentProgress = if (duration > 0) (current / duration).toFloat() else 0f
            delay(1000)
        }
    }



    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        UIKitView(
            factory = {
                val playerContainer = UIView()
                avPlayerViewController.showsPlaybackControls = false
                playerContainer.addSubview(avPlayerViewController.view)
                playerContainer
            },
            modifier = Modifier.fillMaxSize().clickable { isPause = !isPause },
            update = {
                if (isPause) player.pause()
                else player.play()
            },
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
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

        ProgressBar(
            progress = { currentProgress },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .padding(bottom = 5.dp, start = 2.dp, end = 2.dp)
                .onGloballyPositioned {
                    barWidth = it.size.width.toFloat()
                }
                .pointerInput(barWidth) {
                    detectTapGestures { offset ->
                        if (duration > 0 && barWidth > 0f) {
                            val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                            val seekSeconds = newProgress * duration
                            player.seekToTime(
                                CMTimeMakeWithSeconds(
                                    seekSeconds,
                                    preferredTimescale = 1
                                )
                            )
                            player.play()
                        }
                    }
                },
            trackColor = Theme.colorScheme.primary.onPrimaryHint,
            color = Theme.colorScheme.border.brand
        )
    }



    /*
    I need handle like android :
       1- loading
       2- custom progress bar
       3- remove any controllers (play, next ,..) and stop the video when click to any place in screen
       4- stop video when being in background and when return resume when stop using seek to
     */

}