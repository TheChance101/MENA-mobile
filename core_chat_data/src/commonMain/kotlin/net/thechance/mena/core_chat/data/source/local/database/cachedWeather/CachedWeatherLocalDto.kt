package net.thechance.mena.core_chat.data.source.local.database.cachedWeather

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(
    tableName = "cached_weather" ,
    primaryKeys = ["longitude", "latitude"]
)
data class CachedWeatherLocalDto(
    val longitude: Double,
    val latitude: Double,
    @ColumnInfo(name = "current_temperature")
    val currentTemperature: Double,
    @ColumnInfo(name = "min_temperature")
    val minTemperature: Double,
    @ColumnInfo(name = "max_temperature")
    val maxTemperature: Double,
    @ColumnInfo(name = "weather_type")
    val weatherType: String,
    @ColumnInfo(name = "added_at")
    val addedAt: Long = Clock.System.now().toEpochMilliseconds()
)