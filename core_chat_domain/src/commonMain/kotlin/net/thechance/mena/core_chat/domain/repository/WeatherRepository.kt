package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.WeatherDetails

interface WeatherRepository {
    suspend fun getWeatherDetails(latitude: Double, longitude: Double): WeatherDetails
}
