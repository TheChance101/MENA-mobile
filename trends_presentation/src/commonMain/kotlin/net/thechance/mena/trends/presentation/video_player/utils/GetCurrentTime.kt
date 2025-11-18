package net.thechance.mena.trends.presentation.video_player.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getCurrentTime() =
    Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())