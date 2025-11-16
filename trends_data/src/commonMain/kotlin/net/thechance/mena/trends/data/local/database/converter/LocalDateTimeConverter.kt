package net.thechance.mena.trends.data.local.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class LocalDateTimeConverter {
    @TypeConverter
    fun toEpochMillis(dateTime: LocalDateTime): Long {
        return dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }

    @TypeConverter
    fun fromEpochMillis(value: Long): LocalDateTime {
        return Instant.fromEpochMilliseconds(value).toLocalDateTime(TimeZone.UTC)
    }
}