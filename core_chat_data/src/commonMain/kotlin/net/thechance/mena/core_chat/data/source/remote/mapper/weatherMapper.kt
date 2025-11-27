package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.WeatherDetailsDto
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.entity.WeatherType

fun WeatherDetailsDto.toEntity(): WeatherDetails = WeatherDetails(
    currentTemperature = currentTemperature,
    minTemperature = minTemperature,
    maxTemperature = maxTemperature,
    weatherType = toWeatherType(weatherType)
)

private fun toWeatherType(weatherType: String): WeatherType {
    return runCatching {
        WeatherType.valueOf(weatherType)
    }.getOrDefault(WeatherType.UNKNOWN)
}

fun WeatherDetailsDto.toLocalWeather(latitude: Double, longitude: Double): CachedWeatherLocalDto {
    return CachedWeatherLocalDto(
        longitude = longitude,
        latitude = latitude,
        currentTemperature = this.currentTemperature,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherType = this.weatherType
    )
}
