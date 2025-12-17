package net.thechance.mena.core_chat.data.weather

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.mockClient
import net.thechance.mena.core_chat.data.mockRespond
import net.thechance.mena.core_chat.data.repository.WeatherDetailsRepositoryImpl
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherDao
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.WeatherDetailsDto
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.entity.WeatherType
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WeatherDetailsRepositoryImplTest {

    private val mockDao = mock<CachedWeatherDao>()
    private val mockHttpClientHolder = mock<HttpClientHolder>()

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
    fun `should fetch from remote when local cache is empty`() = runTest {
        every { mockHttpClientHolder.getClient() } returns mockClient {
            mockRespond(dummyWeatherDetailsDto)
        }
        everySuspend { mockDao.getWeatherByLocation(any(), any()) } returns null
        everySuspend { mockDao.insertWeather(any()) } returns Unit

        val repository = WeatherDetailsRepositoryImpl(
            clientHolder = mockHttpClientHolder,
            weatherDao = mockDao
        )

        val result = repository.getWeatherDetails(1.0, 1.0)

        assertThat(result).isEqualTo(dummyWeatherDetails)
    }

    @Test
    fun `should return cached data when valid`() = runTest {
        val validCachedWeather = CachedWeatherLocalDto(
            longitude = 1.0,
            latitude = 1.0,
            currentTemperature = 1.0,
            minTemperature = 1.0,
            maxTemperature = 1.0,
            weatherType = "CLEAR_SKY",
            addedAt = Clock.System.now().toEpochMilliseconds()
        )

        everySuspend { mockDao.getWeatherByLocation(any(), any()) } returns validCachedWeather
        every { mockHttpClientHolder.getClient() } returns mockClient {
            mockRespond(dummyWeatherDetailsDto)
        }
        val repository = WeatherDetailsRepositoryImpl(
            clientHolder = mockHttpClientHolder,
            weatherDao = mockDao
        )

        val result = repository.getWeatherDetails(1.0, 1.0)

        assertThat(result).isEqualTo(dummyWeatherDetails)
    }


    @Test
    fun `should fetch from remote when cached data is expired`() = runTest {
        val oneHourAgo = Clock.System.now().toEpochMilliseconds() - (60 * 60 * 1000 + 1000)
        val expiredCachedWeather = CachedWeatherLocalDto(
            longitude = 1.0,
            latitude = 1.0,
            currentTemperature = 5.0,
            minTemperature = 5.0,
            maxTemperature = 5.0,
            weatherType = "CLOUDY",
            addedAt = oneHourAgo
        )

        everySuspend { mockDao.getWeatherByLocation(any(), any()) } returns expiredCachedWeather
        everySuspend { mockDao.insertWeather(any()) } returns Unit
        every { mockHttpClientHolder.getClient() } returns mockClient {
            mockRespond(dummyWeatherDetailsDto)
        }

        val repository = WeatherDetailsRepositoryImpl(
            clientHolder = mockHttpClientHolder,
            weatherDao = mockDao
        )

        val result = repository.getWeatherDetails(1.0, 1.0)

        assertThat(result).isEqualTo(dummyWeatherDetails)
    }
}