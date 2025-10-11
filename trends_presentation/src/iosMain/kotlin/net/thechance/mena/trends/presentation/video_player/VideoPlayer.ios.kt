package net.thechance.mena.trends.presentation.video_player


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    playWhenVisible: Boolean,
    onControllerVisibilityChanged: (Boolean) -> Unit
) {
    var lastPosition by rememberSaveable(url) { mutableStateOf(0.0) }
    val player = remember { AVPlayer() }
    val videoUrl = NSURL.URLWithString(url)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        UIKitView(
            factory = {
                if (videoUrl != null) {
                    val item = AVPlayerItem(uRL = videoUrl)
                    player.replaceCurrentItemWithPlayerItem(item)
                }

                val controller = AVPlayerViewController().apply {
                    this.player = player
                    this.showsPlaybackControls = true
                    this.updatesNowPlayingInfoCenter = false
                    this.requiresLinearPlayback = false
                    this.allowsPictureInPicturePlayback = false

                }

                controller.view
            },
            modifier = Modifier.fillMaxSize(),
            update = { _ ->
                if (playWhenVisible) {
                    player.seekToTime(CMTimeMakeWithSeconds(lastPosition, 600))
                    player.play()
                } else {
                    val current = player.currentTime()
                    lastPosition = CMTimeGetSeconds(current)
                    player.pause()
                }
            },
//            onRelease = {
//                val current = player.currentTime()
//                lastPosition = CMTimeGetSeconds(current)
//                player.pause()
//                player.replaceCurrentItemWithPlayerItem(null)
//            }
        )
    }

    DisposableEffect(Unit) {
        val center = NSNotificationCenter.defaultCenter

        val willResignObserver = center.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = null
        ) { _ ->
            val current = player.currentTime()
            lastPosition = CMTimeGetSeconds(current)
            player.pause()
        }

        val didBecomeActiveObserver = center.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = null
        ) { _ ->
            player.seekToTime(CMTimeMakeWithSeconds(lastPosition, 600))
            if (playWhenVisible) {
                player.play()
            }
        }

        onDispose {
            center.removeObserver(willResignObserver)
            center.removeObserver(didBecomeActiveObserver)
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
        }
    }
}