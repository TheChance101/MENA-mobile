package net.thechance.mena.trends.presentation.video_player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.trends.presentation.screen.user_trend.TrendWatchSessionState

@Composable
expect fun VideoPlayer(
    url: String,
    isTrendVisible: Boolean,
    modifier: Modifier,
    cacheKey: String? = null,
    onVideoPlaying: () -> Unit,
    onRequestRefresh: () -> Unit,
    onNetworkError: () -> Unit,
    saveTrendWatchSession: (TrendWatchSessionState) -> Unit,
    content: @Composable () -> Unit
)