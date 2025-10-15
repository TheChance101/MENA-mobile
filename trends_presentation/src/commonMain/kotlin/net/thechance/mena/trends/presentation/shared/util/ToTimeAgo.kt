package net.thechance.mena.trends.presentation.shared.util

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

@OptIn(ExperimentalTime::class)
fun LocalDateTime.timeAgoValue(): TimeAgoValue {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val duration =
        now.toInstant(TimeZone.currentSystemDefault()) - this.toInstant(TimeZone.currentSystemDefault())

    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours
    val days = duration.inWholeDays

    return when {
        minutes < 1 -> TimeAgoValue.JustNow
        minutes < 60 -> TimeAgoValue.Minutes(minutes)
        hours < 24 -> TimeAgoValue.Hours(hours)
        days < 7 -> TimeAgoValue.Days(days)
        days < 30 -> TimeAgoValue.Weeks(days / 7)
        days < 365 -> TimeAgoValue.Months(days / 30)
        else -> TimeAgoValue.Years(days / 365)
    }
}

sealed interface TimeAgoValue {
    data object JustNow : TimeAgoValue
    data class Minutes(val value: Long) : TimeAgoValue
    data class Hours(val value: Long) : TimeAgoValue
    data class Days(val value: Long) : TimeAgoValue
    data class Weeks(val value: Long) : TimeAgoValue
    data class Months(val value: Long) : TimeAgoValue
    data class Years(val value: Long) : TimeAgoValue
}

@Composable
fun TimeAgoValue.asString(): String {
    return when (this) {
        is TimeAgoValue.JustNow -> stringResource(Res.string.just_now)
        is TimeAgoValue.Minutes -> stringResource(Res.string.minutes_ago, value)
        is TimeAgoValue.Hours -> stringResource(Res.string.hours_ago, value)
        is TimeAgoValue.Days -> stringResource(Res.string.days_ago, value)
        is TimeAgoValue.Weeks -> stringResource(Res.string.weeks_ago, value)
        is TimeAgoValue.Months -> stringResource(Res.string.months_ago, value)
        is TimeAgoValue.Years -> stringResource(Res.string.years_ago, value)
    }
}