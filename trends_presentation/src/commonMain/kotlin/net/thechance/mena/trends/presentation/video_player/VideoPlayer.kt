package net.thechance.mena.trends.presentation.video_player

import androidx.compose.runtime.Composable


@Composable
expect fun VideoPlayer(
    url : String,
    playWhenVisible: Boolean,
    autoPlay: Boolean,
)