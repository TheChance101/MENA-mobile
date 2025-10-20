package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.time_days_ago
import mena.faith_presentation.generated.resources.time_hours_ago
import mena.faith_presentation.generated.resources.time_minutes_ago
import mena.faith_presentation.generated.resources.time_months_ago
import mena.faith_presentation.generated.resources.time_seconds_ago
import mena.faith_presentation.generated.resources.time_weeks_ago
import mena.faith_presentation.generated.resources.time_years_ago
import net.thechance.mena.faith.presentation.feature.quran.bookmark.TimeAgo
import net.thechance.mena.faith.presentation.feature.quran.bookmark.TimeUnit
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun TimeAgo.getTimeAgo(): String {
    return when (unit) {
        TimeUnit.SECONDS -> pluralStringResource(Res.plurals.time_seconds_ago, amount, amount)
        TimeUnit.MINUTES -> pluralStringResource(Res.plurals.time_minutes_ago, amount, amount)
        TimeUnit.HOURS -> pluralStringResource(Res.plurals.time_hours_ago, amount, amount)
        TimeUnit.DAYS -> pluralStringResource(Res.plurals.time_days_ago, amount, amount)
        TimeUnit.WEEKS -> pluralStringResource(Res.plurals.time_weeks_ago, amount, amount)
        TimeUnit.MONTHS -> pluralStringResource(Res.plurals.time_months_ago, amount, amount)
        TimeUnit.YEARS -> pluralStringResource(Res.plurals.time_years_ago, amount, amount)
    }
}


@OptIn(ExperimentalTime::class)
fun Instant.calculateTimeAgo(now: Instant = Clock.System.now()): TimeAgo {
    val duration: Duration = now - this

    return when {
        duration.inWholeSeconds < ONE_MINUTE_IN_SECONDS ->
            TimeAgo(
                duration.inWholeSeconds.toInt(),
                TimeUnit.SECONDS
            )

        duration.inWholeMinutes < ONE_HOUR_IN_MINUTES ->
            TimeAgo(
                duration.inWholeMinutes.toInt(),
                TimeUnit.MINUTES
            )

        duration.inWholeHours < ONE_DAY_IN_HOURS ->
            TimeAgo(
                duration.inWholeHours.toInt(),
                TimeUnit.HOURS
            )

        duration.inWholeDays < ONE_WEEK_IN_DAYS ->
            TimeAgo(
                duration.inWholeDays.toInt(),
                TimeUnit.DAYS
            )

        duration.inWholeDays < ONE_MONTH_IN_DAYS ->
            TimeAgo(
                (duration.inWholeDays / ONE_WEEK_IN_DAYS).toInt(),
                TimeUnit.WEEKS
            )

        duration.inWholeDays < ONE_YEAR_IN_DAYS ->
            TimeAgo(
                (duration.inWholeDays / ONE_MONTH_IN_DAYS).toInt(),
                TimeUnit.MONTHS
            )

        else -> TimeAgo(
            (duration.inWholeDays / ONE_YEAR_IN_DAYS).toInt(),
            TimeUnit.YEARS
        )
    }
}

private const val ONE_MINUTE_IN_SECONDS = 60
private const val ONE_HOUR_IN_MINUTES = 60
private const val ONE_DAY_IN_HOURS = 24
private const val ONE_WEEK_IN_DAYS = 7
private const val ONE_MONTH_IN_DAYS = 30
private const val ONE_YEAR_IN_DAYS = 365
