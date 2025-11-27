package net.thechance.mena.core_chat.data.source.local.database.cachedWeather

import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.entity.WeatherType

fun CachedWeatherLocalDto.toEntity(): WeatherDetails {
    return WeatherDetails(
        currentTemperature = this.currentTemperature,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherType = toWeatherType(this.weatherType)
    )
}

private fun toWeatherType(weatherType: String): WeatherType {
    return runCatching {
        WeatherType.valueOf(weatherType)
    }.getOrDefault(WeatherType.UNKNOWN)
}
