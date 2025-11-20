package net.thechance.mena.faith.data.repository

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto
import net.thechance.mena.faith.data.remote.model.mosque.MosqueResponseDto
import net.thechance.mena.faith.data.remote.service.MosqueApiService
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.domain.repository.MosqueRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalUuidApi::class)
class MosqueRepositoryImplTest {

    private val mosqueApiService: MosqueApiService = mock(MockMode.autofill)
    private lateinit var repository: MosqueRepository


    @BeforeTest
    fun setup() {
        repository = MosqueRepositoryImpl(mosqueApiService = mosqueApiService)
    }

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
    @Test
    fun `addMosque should call API successfully when valid data is provided`() = runTest {
        // Given
        everySuspend { mosqueApiService.createMosque(any()) } returns successfulAddMosqueResponse()

        val mosque = FAKE_MOSQUE
        val imageBytes = ByteArray(10) { 1 }

        // When
        repository.addMosque(mosque, imageBytes)

        // Then
        verifySuspend {
            mosqueApiService.createMosque(any())
        }
    }

    @Test
    fun `addMosque should handle API failure gracefully`() = runTest {
        // Given
        everySuspend { mosqueApiService.createMosque(any()) } throws Exception("Network error")

        val mosque = FAKE_MOSQUE
        val imageBytes = ByteArray(10)

        // When
        val result = runCatching { repository.addMosque(mosque, imageBytes) }

        // Then
        assertThat(result).isFailure()
    }

    @Test
    fun `addMosque should handle empty name correctly`() = runTest {
        // Given
        val invalidMosque = FAKE_MOSQUE.copy(name = "")
        everySuspend { mosqueApiService.createMosque(any()) } returns successfulAddMosqueResponse()

        // When
        val result = runCatching { repository.addMosque(invalidMosque, ByteArray(10)) }

        // Then
        assertThat(result).isSuccess()
    }

    @Test
    fun `addMosque should handle empty address correctly`() = runTest {
        // Given
        val invalidMosque = FAKE_MOSQUE.copy(address = "")
        everySuspend { mosqueApiService.createMosque(any()) } returns successfulAddMosqueResponse()

        // When
        val result = runCatching { repository.addMosque(invalidMosque, ByteArray(10)) }

        // Then
        assertThat(result).isSuccess()
    }

    @Test
    fun `addMosque should handle invalid coordinates gracefully`() = runTest {
        // Given
        val invalidMosque = FAKE_MOSQUE.copy(
            coordinates = Mosque.Coordinates(latitude = 200.0, longitude = -190.0)
        )

        // When
        val result = runCatching { repository.addMosque(invalidMosque, ByteArray(10)) }

        // Then
        assertThat(result).isFailure()
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

    private fun successfulAddMosqueResponse(): Response<MosqueResponseDto> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }
        return Response.success(
            body = FAKE_MOSQUE_DTO,
            rawResponse = mockHttpResponse
        ) as Response<MosqueResponseDto>
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
        val FAKE_MOSQUE = Mosque(
            id = Uuid.random(),
            name = "Masjid Al-Noor",
            address = "Baghdad Street 1",
            coordinates = Mosque.Coordinates(33.33, 44.44),
            imageUrl = "https://example.com/mosque.jpg"
        )

        val FAKE_MOSQUE_DTO = Mosque(
            id = Uuid.random(),
            name = "Masjid Al-Noor",
            address = "Baghdad Street 1",
            coordinates = Mosque.Coordinates(33.33, 44.44),
            imageUrl = "https://example.com/mosque.jpg"
        )

        private val MOCK_NEARBY_MOSQUES = MOCK_MOSQUE_DTO_LIST
    }
}