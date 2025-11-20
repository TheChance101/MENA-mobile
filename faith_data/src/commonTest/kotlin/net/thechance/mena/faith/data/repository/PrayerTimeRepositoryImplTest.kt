package net.thechance.mena.faith.data.repository

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto
import net.thechance.mena.faith.data.remote.service.PrayerTimeApiService
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimeRepositoryImplTest {

    private val prayerTimeApiService: PrayerTimeApiService = mock(MockMode.autofill)
    private val prayerTimeRepository = PrayerTimeRepositoryImpl(prayerTimeApiService)

    @Test
    fun `getPrayerTimes should return list of PrayerTime when api service return valid data`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG
                )
            } returns makeSuccessFakeResponse(
                body = fakePrayerTimesDto,
                successStatus = HttpStatusCode.OK
            )

            val result = prayerTimeRepository.getPrayerTimes(
                date = dateInstant,
                address = address,
                timeZone = timeZone
            )

            assertThat(result).isEqualTo(fakePrayerTimes)
        }

    @Test
    fun `getPrayerTimes should throw NetworkException when error occur in parsing response body is null`() =
        runTest {

            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG
                )
            } returns makeSuccessFakeResponse(
                body = null,
                successStatus = HttpStatusCode.OK
            )

            assertFailure {
                prayerTimeRepository.getPrayerTimes(
                    date = dateInstant,
                    address = address,
                    timeZone = timeZone
                )
            }.isInstanceOf<FaithException.NetworkException>()
        }

    @Test
    fun `getPrayerTimes should throw UnauthorizedException when status code is Unauthorized`() =
        runTest {

            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.Unauthorized)

            assertFailure {
                prayerTimeRepository.getPrayerTimes(
                    date = dateInstant,
                    address = address,
                    timeZone = timeZone
                )
            }.isInstanceOf<FaithException.UnauthorizedException>()
        }

    @Test
    fun `getPrayerTimes should throw NetworkException when status code is InternalServerError`() =
        runTest {

            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.InternalServerError)

            assertFailure {
                prayerTimeRepository.getPrayerTimes(
                    date = dateInstant,
                    address = address,
                    timeZone = timeZone
                )
            }.isInstanceOf<FaithException.NetworkException>()
        }

    @Test
    fun `getPrayerTimes should throw UnknownException when status code is not valuable`() =
        runTest {

            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.Forbidden)

            assertFailure {
                prayerTimeRepository.getPrayerTimes(
                    date = dateInstant,
                    address = address,
                    timeZone = timeZone
                )
            }.isInstanceOf<FaithException.UnknownException>()
        }

    private fun makeSuccessFakeResponse(
        body: PrayerTimesDto? = fakePrayerTimesDto,
        successStatus: HttpStatusCode = HttpStatusCode.OK
    ): Response<PrayerTimesDto> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns successStatus
        }

        return Response.success(
            body = body,
            rawResponse = mockHttpResponse
        ) as Response<PrayerTimesDto>
    }

    private fun makeFailFakeResponse(
        errorStatus: HttpStatusCode = HttpStatusCode.InternalServerError
    ): Response<PrayerTimesDto> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns errorStatus
        }

        return Response.error<PrayerTimesDto>(
            rawResponse = mockHttpResponse,
            body = ""
        ) as Response<PrayerTimesDto>
    }

    @Test
    fun `getPrayerTimeWithHijriDate should return list of PrayerTime when api service return valid data with Hijri date`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = HIJRI_DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = true
                )
            } returns makeSuccessFakeResponse(
                body = fakePrayerTimesDto,
                successStatus = HttpStatusCode.OK
            )

            val result = prayerTimeRepository.getPrayerTimeWithHijriDate(
                date = HIJRI_DATE,
                address = address,
                timeZone = timeZone,
                isHijri = true
            )

            assertThat(result).isEqualTo(fakePrayerTimes)
        }

    @Test
    fun `getPrayerTimeWithHijriDate should return list of PrayerTime when api service return valid data with Gregorian date`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = false
                )
            } returns makeSuccessFakeResponse(
                body = fakePrayerTimesDto,
                successStatus = HttpStatusCode.OK
            )

            val result = prayerTimeRepository.getPrayerTimeWithHijriDate(
                date = DATE,
                address = address,
                timeZone = timeZone,
                isHijri = false
            )

            assertThat(result).isEqualTo(fakePrayerTimes)
        }

    @Test
    fun `getPrayerTimeWithHijriDate should throw NetworkException when response body is null`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = HIJRI_DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = true
                )
            } returns makeSuccessFakeResponse(
                body = null,
                successStatus = HttpStatusCode.OK
            )

            assertFailure {
                prayerTimeRepository.getPrayerTimeWithHijriDate(
                    date = HIJRI_DATE,
                    address = address,
                    timeZone = timeZone,
                    isHijri = true
                )
            }.isInstanceOf<FaithException.NetworkException>()
        }

    @Test
    fun `getPrayerTimeWithHijriDate should throw UnauthorizedException when status code is Unauthorized`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = HIJRI_DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = true
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.Unauthorized)

            assertFailure {
                prayerTimeRepository.getPrayerTimeWithHijriDate(
                    date = HIJRI_DATE,
                    address = address,
                    timeZone = timeZone,
                    isHijri = true
                )
            }.isInstanceOf<FaithException.UnauthorizedException>()
        }

    @Test
    fun `getPrayerTimeWithHijriDate should throw NetworkException when status code is InternalServerError`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = HIJRI_DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = true
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.InternalServerError)

            assertFailure {
                prayerTimeRepository.getPrayerTimeWithHijriDate(
                    date = HIJRI_DATE,
                    address = address,
                    timeZone = timeZone,
                    isHijri = true
                )
            }.isInstanceOf<FaithException.NetworkException>()
        }

    @Test
    fun `getPrayerTimeWithHijriDate should throw UnknownException when status code is not valuable`() =
        runTest {
            everySuspend {
                prayerTimeApiService.getPrayerTimes(
                    date = HIJRI_DATE,
                    latitude = LAT,
                    longitude = LONG,
                    isHijri = true
                )
            } returns makeFailFakeResponse(errorStatus = HttpStatusCode.Forbidden)

            assertFailure {
                prayerTimeRepository.getPrayerTimeWithHijriDate(
                    date = HIJRI_DATE,
                    address = address,
                    timeZone = timeZone,
                    isHijri = true
                )
            }.isInstanceOf<FaithException.UnknownException>()
        }

    @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
    private companion object {
        val timeZone = TimeZone.of("Africa/Cairo")
        const val HIJRI_DATE = "01-10-1447"
        const val LAT = 30.033333
        const val LONG = 31.233334
        const val DATE = "2025-10-10"
        val dateInstant = Instant.parse("2025-10-10T00:00:00Z") //10-10-2025 00:00
        val fakePrayerTimesDto = getFakePrayerTimesDto()
        val fakePrayerTimes: List<PrayerTime> = getPrayerTimesFakeData(timeZone = timeZone)
        val address: Address = Address(
            id = null,
            addressLine = "",
            addressType = AddressType.Home,
            latitude = LAT,
            longitude = LONG
        )
    }
}
