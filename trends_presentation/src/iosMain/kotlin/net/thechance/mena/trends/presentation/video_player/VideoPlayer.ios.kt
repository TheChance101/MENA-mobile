package net.thechance.mena.trends.presentation.video_player


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
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.ic_pause
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.progressBar.ProgressBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
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
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIStackView
import platform.darwin.Float64
import platform.darwin.NSObjectProtocol

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

    // Handle sound in background
    DisposableEffect(Unit) {
        val center = NSNotificationCenter.defaultCenter

        val backgroundObserver: NSObjectProtocol = center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            lastPosition = CMTimeGetSeconds(player.currentTime())
            player.pause()
        }

        val foregroundObserver: NSObjectProtocol = center.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            if (playWhenVisible) {
                player.seekToTime(CMTimeMakeWithSeconds(lastPosition, preferredTimescale = 1))
                player.play()
            }
        }

        onDispose {
            center.removeObserver(backgroundObserver)
            center.removeObserver(foregroundObserver)
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        UIKitView(
            factory = {
                val stackView = UIStackView()
                stackView.translatesAutoresizingMaskIntoConstraints = false

                val playerView = avPlayerViewController.view
                avPlayerViewController.showsPlaybackControls = false

                stackView.addSubview(playerView)
                stackView
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPause = !isPause },
            update = {
                if (isPause) player.pause()
                else player.play()
            },
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )

        if (isPause) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pause),
                    contentDescription = "Pause Icon"
                )
            }
        }

        if (isLoading && !isPause) {
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
                .padding(bottom = 1.dp, start = 1.dp, end = 1.dp)
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
                        }
                    }
                },
            trackColor = Theme.colorScheme.primary.onPrimaryHint,
            color = Theme.colorScheme.border.brand
        )
    }


    /*
    I need handle like android :
       1- loading and scroll issue( prevent to load video first then scroll )
       2- custom progress bar (Done)
       3- remove any controllers (play, next ,..) and stop the video when click to any place in screen (Done)
       4- stop video when being in background and when return resume when stop using seek to (Done)
     */
}