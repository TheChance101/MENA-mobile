package net.thechance.mena.trends.presentation.shared.util.extention

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.days_ago
import mena.trends_presentation.generated.resources.hours_ago
import mena.trends_presentation.generated.resources.just_now
import mena.trends_presentation.generated.resources.minutes_ago
import mena.trends_presentation.generated.resources.months_ago
import mena.trends_presentation.generated.resources.weeks_ago
import mena.trends_presentation.generated.resources.years_ago
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
@OptIn(ExperimentalTime::class)
fun LocalDateTime.toTimeAgo(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val duration = now.toInstant(TimeZone.currentSystemDefault()) -
            this.toInstant(TimeZone.currentSystemDefault())

    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours
    val days = duration.inWholeDays

    return when {
        minutes < 1 -> stringResource(Res.string.just_now)
        minutes < 60 -> stringResource(Res.string.minutes_ago, minutes)
        hours < 24 -> stringResource(Res.string.hours_ago, hours)
        days < 7 -> stringResource(Res.string.days_ago, days)
        days < 30 -> stringResource(Res.string.weeks_ago, days / 7)
        days < 365 -> stringResource(Res.string.months_ago, days / 30)
        else -> stringResource(Res.string.years_ago, days / 365)
    }
}