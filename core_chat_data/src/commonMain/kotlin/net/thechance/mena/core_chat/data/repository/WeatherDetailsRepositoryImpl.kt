package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.remote.dto.WeatherDetailsDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toEntity
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.repository.WeatherRepository

class WeatherDetailsRepositoryImpl(
    private val client: HttpClient,
) : WeatherRepository {

    override suspend fun getWeatherDetails(latitude: Double, longitude: Double): WeatherDetails {
        return tryNetworkCall<WeatherDetailsDto>(bodyType = typeInfo<WeatherDetailsDto>()) {
            client.get("/weather/current") {
                parameter(key = "lat", value = latitude)
                parameter(key = "lng", value = longitude)
            }
        }.toEntity()
    }
}
