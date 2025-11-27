package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherDao
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.toEntity
import net.thechance.mena.core_chat.data.source.remote.dto.WeatherDetailsDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toEntity
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalWeather
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.repository.WeatherRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WeatherDetailsRepositoryImpl(
    private val clientHolder: HttpClientHolder,
    private val weatherDao: CachedWeatherDao
) : WeatherRepository {
    private val client: HttpClient
        get() = clientHolder.getClient()
    override suspend fun getWeatherDetails(latitude: Double, longitude: Double): WeatherDetails {
        val localWeather = getLocalWeatherOrNull(latitude = latitude,longitude = longitude)
        if (localWeather != null)
            return localWeather.toEntity()
        try {
            val remoteWeather =
                tryNetworkCall<WeatherDetailsDto>(bodyType = typeInfo<WeatherDetailsDto>()) {
                    client.get(CURRENT_WEATHER_END_POINT) {
                        parameter(key = "lat", value = latitude)
                        parameter(key = "lng", value = longitude)
                    }
                }

            weatherDao.insertWeather(remoteWeather.toLocalWeather(latitude = latitude, longitude = longitude))
            return remoteWeather.toEntity()
        }catch (_: NoInternetException){
            val localWeather = weatherDao.getWeatherByLocation(latitude = latitude, longitude = longitude)
            return localWeather?.toEntity() ?: throw NoInternetException()
        }
    }

    private suspend fun getLocalWeatherOrNull(latitude: Double, longitude: Double): CachedWeatherLocalDto?{
        val localWeather = weatherDao.getWeatherByLocation(latitude = latitude, longitude = longitude)
        if (localWeather != null){
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val oneHourInMillis = TIME_TO_UPDATE_IN_MS
            val timeDiff = currentTime - localWeather.addedAt
            if (timeDiff <= oneHourInMillis) return localWeather
        }
        return null
    }

    companion object {
        const val TIME_TO_UPDATE_IN_MS = 60 * 60 * 1000
        const val CURRENT_WEATHER_END_POINT = "/weather/current"
    }
}
