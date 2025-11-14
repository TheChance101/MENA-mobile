package net.thechance.mena.faith.data.repository

import assertk.assertThat
import assertk.assertions.isFailure
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
import net.thechance.mena.faith.data.remote.model.mosque.MosqueResponseDto
import net.thechance.mena.faith.data.remote.service.MosqueApiService
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalUuidApi::class)
class MosqueRepositoryImplTest {

    private val apiService: MosqueApiService = mock(MockMode.autofill)
    private lateinit var repository: MosqueRepository

    @BeforeTest
    fun setup() {
        repository = MosqueRepositoryImpl(mosqueApiService = apiService)
    }


    @Test
    fun `addMosque should call API successfully when valid data is provided`() = runTest {
        // Given
        everySuspend { apiService.createMosque(any()) } returns successfulAddMosqueResponse()

        val mosque = FAKE_MOSQUE
        val imageBytes = ByteArray(10) { 1 }

        // When
        repository.addMosque(mosque, imageBytes)

        // Then
        verifySuspend {
            apiService.createMosque(any())
        }
    }

    @Test
    fun `addMosque should handle API failure gracefully`() = runTest {
        // Given
        everySuspend { apiService.createMosque(any()) } throws Exception("Network error")

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
        everySuspend { apiService.createMosque(any()) } returns successfulAddMosqueResponse()

        // When
        val result = runCatching { repository.addMosque(invalidMosque, ByteArray(10)) }

        // Then
        assertThat(result).isSuccess()
    }

    @Test
    fun `addMosque should handle empty address correctly`() = runTest {
        // Given
        val invalidMosque = FAKE_MOSQUE.copy(address = "")
        everySuspend { apiService.createMosque(any()) } returns successfulAddMosqueResponse()

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


    private fun successfulAddMosqueResponse(): Response<MosqueResponseDto> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }
        return Response.success(
            body = FAKE_MOSQUE_DTO,
            rawResponse = mockHttpResponse
        ) as Response<MosqueResponseDto>
    }

    private companion object {

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
    }
}
