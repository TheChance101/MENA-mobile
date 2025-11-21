package net.thechance.mena.core_chat.data.weather

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.mockClient
import net.thechance.mena.core_chat.data.mockRespond
import net.thechance.mena.core_chat.data.repository.WeatherDetailsRepositoryImpl
import net.thechance.mena.core_chat.data.source.remote.dto.WeatherDetailsDto
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.entity.WeatherType
import kotlin.test.Test

class WeatherDetailsRepositoryImplTest {
    private val dummyWeatherDetailsDto = WeatherDetailsDto(
        currentTemperature = 1.0,
        minTemperature = 1.0,
        maxTemperature = 1.0,
        weatherType = "CLEAR_SKY"
    )

    private val dummyWeatherDetails = WeatherDetails(
        currentTemperature = 1.0,
        minTemperature = 1.0,
        maxTemperature = 1.0,
        weatherType = WeatherType.CLEAR_SKY
    )

    @Test
    fun `getWeatherDetails should return weather details`() = runTest {
        val repository = WeatherDetailsRepositoryImpl(
            client = mockClient { mockRespond(dummyWeatherDetailsDto) }
        )

        val result = repository.getWeatherDetails(1.0, 1.0)

        assertThat(result).isEqualTo(dummyWeatherDetails)
    }
}
