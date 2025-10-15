package net.thechance.mena.trends.presentation.video_player


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.zIndex
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    playWhenVisible: Boolean,
) {
    val player = remember(url) { AVPlayer(uRL = NSURL.URLWithString(url)!!) }
    val controller = remember { AVPlayerViewController().apply { this.player = player } }

    var isPause by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableStateOf(0f) }
    var duration by remember { mutableDoubleStateOf(1.0) }

    LaunchedEffect(playWhenVisible) {
        if (playWhenVisible) player.play() else player.pause()
    }

    LaunchedEffect(url) {
        val item = player.currentItem
        if (item != null) {
            duration = CMTimeGetSeconds(item.asset.duration)
            isLoading = false
        }
        while (isActive) {
            val current = CMTimeGetSeconds(player.currentTime())
            currentProgress = if (duration > 0) (current / duration).toFloat() else 0f
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
            controller.player = null
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = { controller.view },
            update = {
                if (isPause) player.pause() else player.play()
            }
        )


        Box(
            modifier = Modifier
                .matchParentSize()
                .zIndex(10f)
                .background(Color.Transparent)
                .clickable { isPause = !isPause }
        )

        if (isPause) {
            Icon(
                painter = painterResource(Res.drawable.ic_pause),
                contentDescription = "Pause",
                modifier = Modifier.align(Alignment.Center).zIndex(11f)
            )
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
                .padding(2.dp),
            trackColor = Theme.colorScheme.primary.onPrimaryHint,
            color = Theme.colorScheme.border.brand
        )
    }
}