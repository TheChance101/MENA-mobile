package net.thechance.mena.faith.data.repository

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isEmpty
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto
import net.thechance.mena.faith.data.remote.service.MosqueApiService
import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.test.Test

class MosqueRepositoryImplTest {

    private val mosqueApiService: MosqueApiService = mock(MockMode.autofill)
    private val repository = MosqueRepositoryImpl(mosqueApiService)

    @Test
    fun `getMosquesByName should return list of mosques when api returns valid data`() = runTest {
        everySuspend { mosqueApiService.searchMosquesByName("Masjid", 1, 10) } returns
                makeSuccessResponse(MOCK_MOSQUE_PAGE_RESPONSE)

        val result = repository.getMosquesByName("Masjid", 1, 10)

        assertThat(result.size).isEqualTo(2)
        assertThat(result.first().name).isEqualTo("Masjid Salah")
    }

    @Test
    fun `getMosquesByName should return empty list when api returns empty page`() = runTest {
        val emptyPageResponse = PageResponse<MosqueDto>(
            currentPage = 1,
            items = emptyList(),
            totalPages = 0,
            totalItems = 0
        )
        everySuspend { mosqueApiService.searchMosquesByName("NonExistent", 1, 10) } returns
                makeSuccessResponse(emptyPageResponse)

        val result = repository.getMosquesByName("NonExistent", 1, 10)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getMosquesByName should throw UnauthorizedException when status code is Unauthorized`() = runTest {
        everySuspend { mosqueApiService.searchMosquesByName("Masjid", 1, 10) } returns
                makeFailResponse(HttpStatusCode.Unauthorized)

        assertFailure { repository.getMosquesByName("Masjid", 1, 10) }
            .isInstanceOf<FaithException.UnauthorizedException>()
    }

    @Test
    fun `getMosquesByName should throw NetworkException when status code is InternalServerError`() = runTest {
        everySuspend { mosqueApiService.searchMosquesByName("Masjid", 1, 10) } returns
                makeFailResponse(HttpStatusCode.InternalServerError)

        assertFailure { repository.getMosquesByName("Masjid", 1, 10) }
            .isInstanceOf<FaithException.NetworkException>()
    }

    @Test
    fun `getMosquesByName should throw NetworkException when status code is BadRequest`() = runTest {
        everySuspend { mosqueApiService.searchMosquesByName("", 1, 10) } returns
                makeFailResponse(HttpStatusCode.InternalServerError)

        assertFailure { repository.getMosquesByName("", 1, 10) }
            .isInstanceOf<FaithException.NetworkException>()
    }

    @Test
    fun `getMosquesByName should throw NetworkException when status code is NotFound`() = runTest {
        everySuspend { mosqueApiService.searchMosquesByName("Masjid", 999, 10) } returns
                makeFailResponse(HttpStatusCode.InternalServerError)

        assertFailure { repository.getMosquesByName("Masjid", 999, 10) }
            .isInstanceOf<FaithException.NetworkException>()
    }

    @Test
    fun `getNearbyMosques should return list of mosques when api returns valid data`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(30.0, 31.0, 5.0) } returns
                makeSuccessResponse(MOCK_NEARBY_MOSQUES)

        val result = repository.getNearbyMosques(30.0, 31.0, 5.0)

        assertThat(result.size).isEqualTo(2)
        assertThat(result.first().name).isEqualTo("Masjid Salah")
    }

    @Test
    fun `getNearbyMosques should return empty list when no mosques nearby`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(0.0, 0.0, 1.0) } returns
                makeSuccessResponse(emptyList<MosqueDto>())

        val result = repository.getNearbyMosques(0.0, 0.0, 1.0)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getNearbyMosques should throw UnauthorizedException when status code is Unauthorized`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(30.0, 31.0, 5.0) } returns
                makeFailResponse(HttpStatusCode.Unauthorized)

        assertFailure { repository.getNearbyMosques(30.0, 31.0, 5.0) }
            .isInstanceOf<FaithException.UnauthorizedException>()
    }

    @Test
    fun `getNearbyMosques should throw NetworkException when status code is InternalServerError`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(30.0, 31.0, 5.0) } returns
                makeFailResponse(HttpStatusCode.InternalServerError)

        assertFailure { repository.getNearbyMosques(30.0, 31.0, 5.0) }
            .isInstanceOf<FaithException.NetworkException>()
    }

    @Test
    fun `getNearbyMosques should handle edge coordinates`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(-90.0, -180.0, 10.0) } returns
                makeSuccessResponse(emptyList<MosqueDto>())

        val result = repository.getNearbyMosques(-90.0, -180.0, 10.0)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getNearbyMosques should throw NetworkException when status code is BadRequest`() = runTest {
        everySuspend { mosqueApiService.getNearbyMosques(30.0, 31.0, -5.0) } returns
                makeFailResponse(HttpStatusCode.InternalServerError)

        assertFailure { repository.getNearbyMosques(30.0, 31.0, -5.0) }
            .isInstanceOf<FaithException.NetworkException>()
    }


    private fun <T> makeSuccessResponse(
        body: T,
        successStatus: HttpStatusCode = HttpStatusCode.OK
    ): Response<T> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns successStatus
        }
        return Response.success(body, mockHttpResponse) as Response<T>
    }

    private fun <T> makeFailResponse(
        errorStatus: HttpStatusCode
    ): Response<T> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns errorStatus
        }
        return Response.error<T>(rawResponse = mockHttpResponse, body = "") as Response<T>
    }


    companion object {
        private val MOCK_MOSQUE_DTO_LIST = listOf(
            MosqueDto(
                id = "00000000-0000-0000-0000-000000000000",
                name = "Masjid Salah",
                latitude = 30.0,
                longitude = 31.0,
                address = "Cairo",
                imageUrl = "",
                createdAt = "2025-11-01T10:00:00Z"
            ),
            MosqueDto(
                id = "00000000-0000-0000-0000-000000000001",
                name = "Masjid Noor",
                latitude = 30.1,
                longitude = 31.1,
                address = "Giza",
                imageUrl = "",
                createdAt = "2025-11-01T10:00:00Z"
            )
        )

        private val MOCK_MOSQUE_PAGE_RESPONSE = PageResponse(
            currentPage = 1,
            items = MOCK_MOSQUE_DTO_LIST,
            totalPages = 1,
            totalItems = 2
        )

        private val MOCK_NEARBY_MOSQUES = MOCK_MOSQUE_DTO_LIST
    }
}