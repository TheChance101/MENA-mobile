package net.thechance.mena.core_chat.data.source.local.database.cachedWeather

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CachedWeatherDao {
    @Query("SELECT * FROM cached_weather WHERE latitude = :latitude AND longitude = :longitude LIMIT 1")
    suspend fun getWeatherByLocation(latitude: Double, longitude: Double,): CachedWeatherLocalDto?

    @Query("DELETE FROM cached_weather WHERE longitude = :longitude AND latitude = :latitude")
    suspend fun deleteWeatherByLocation(longitude: Double, latitude: Double)

    @Upsert
    suspend fun insertWeather(weather: CachedWeatherLocalDto)

}