package net.thechance.mena.trends.presentation.video_player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.trends.presentation.screen.user_reel.ReelWatchSessionState

@Composable
expect fun VideoPlayer(
    url: String,
    isReelVisible: Boolean,
    modifier: Modifier,
    cacheKey: String? = null,
    onVideoPlaying: () -> Unit,
    onRequestRefresh: () -> Unit,
    onNetworkError: () -> Unit,
    saveReelWatchSession: (ReelWatchSessionState) -> Unit,
    content: @Composable () -> Unit
)