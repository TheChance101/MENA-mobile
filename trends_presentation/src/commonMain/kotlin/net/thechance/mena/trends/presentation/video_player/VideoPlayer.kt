package net.thechance.mena.trends.presentation.video_player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    url: String,
    isReelVisible: Boolean,
    modifier: Modifier,
    cacheKey: String? = null,
    onVideoPlaying: () -> Unit,
    onRequestRefresh: () -> Unit,
    content: @Composable () -> Unit
)