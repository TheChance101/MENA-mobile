package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createDukanManagementRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createDukanRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultCreateResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultDukanDetailsResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultNameAvailableResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultStatusResponse
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DukanManagementRepositoryTest {
    private val dukanManagementRepository: DukanManagementRepositoryImpl =
        createDukanManagementRepository()
    private lateinit var capturedRequest: HttpRequestData

    private fun createMockClient(): HttpClient {
        val mockEngine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """["https://mock/image.jpg"]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `createDukan calls the correct endpoint`() = runTest {
        var called = false
        val repo = createDukanRepository(
            createResponse = {
                called = true
                defaultCreateResponse()
            }
        )

        repo.createDukan(
            Dukan(
                id = Uuid.random(),
                name = "Test Dukan",
                color = Color(Uuid.random(), "#FF0000"),
                style = Dukan.Style.WIDE_IMAGE,
                imageUrl = "",
                categories = emptySet<Category>(),
                coordinates = Dukan.Coordinates(0.0, 0.0),
                address = "",
                status = Dukan.Status.PENDING
            )
        )

        assertTrue(called)
    }

    @Test
    fun `getMyDukanStatus returns mapped status`() = runTest {
        val repo = createDukanRepository(
            statusResponse = { defaultStatusResponse() }
        )

        val status = repo.getMyDukanStatus()

        assertEquals(
            MyDukanStatus(Dukan.Status.PENDING, "Active"),
            status
        )
    }

    @Test
    fun `getCategories returns mapped categories`() = runTest {
        val categories = dukanManagementRepository.getCategories()
        val expectedNames = listOf("Category 1", "Category 2", "Category 3")
        val actualNames = categories.map { it.name }

        assertEquals(expectedNames, actualNames)
    }

    @Test
    fun `getDukanColors returns mapped colors`() = runTest {
        val colors = dukanManagementRepository.getDukanColors()
        val expectedHexCodes = listOf("#FF0000", "#00FF00", "#0000FF")
        val actualHexCodes = colors.map { it.hexCode }

        assertEquals(expectedHexCodes, actualHexCodes)
    }

    @Test
    fun `getDukanStyles returns styles as enums`() = runTest {
        val styles = dukanManagementRepository.getDukanStyles()
        assertEquals(
            listOf(Dukan.Style.WIDE_IMAGE, Dukan.Style.NO_IMAGE),
            styles
        )
    }

    @Test
    fun `isDukanNameTaken returns false for available name`() = runTest {
        val isTaken = dukanManagementRepository.isDukanNameTaken("some_name")
        assertFalse(isTaken)
    }

    @Test
    fun `isDukanNameTaken returns true for taken name`() = runTest {
        val repo = createDukanRepository(
            nameResponse = { defaultNameAvailableResponse(isTaken = true) }
        )
        val isTaken = repo.isDukanNameTaken("taken_name")
        assertTrue(isTaken)
    }

    @Test
    fun `uploadDukanImage returns image URL`() = runTest {
        val url = dukanManagementRepository.uploadDukanImage(
            fileName = "image.png",
            fileBytes = ByteArray(0)
        )
        assertEquals("https://cdn.example.com/dukan/image.png", url)
    }

    @Test
    fun `getDukanDetailsByDukanId should map name correctly`() = runTest {
        // Arrange
        val dukanId = "dukan123"
        val repo = createDukanRepository(dukanDetailsResponse = { defaultDukanDetailsResponse() })

        // Act
        val details = repo.getDukanDetailsByDukanId(dukanId)

        // Assert
        assertEquals("Test Dukan", details.name)
    }

    @Test
    fun `getDukanDetailsByDukanId should map address correctly`() = runTest {
        // Arrange
        val dukanId = "dukan123"
        val repo = createDukanRepository(dukanDetailsResponse = { defaultDukanDetailsResponse() })

        // Act
        val details = repo.getDukanDetailsByDukanId(dukanId)

        // Assert
        assertEquals("123 Test St, Cairo, Egypt", details.address)
    }

    @Test
    fun `getDukanDetailsByDukanId should map color correctly`() = runTest {
        val dukanId = "dukan123"
        val repo = createDukanRepository(dukanDetailsResponse = { defaultDukanDetailsResponse() })

        val details = repo.getDukanDetailsByDukanId(dukanId)

        assertEquals("#FF0000", details.color.hexCode)
    }

    @Test
    fun `getDukanDetailsByDukanId should map style correctly`() = runTest {
        // Arrange
        val dukanId = "dukan123"
        val repo = createDukanRepository(dukanDetailsResponse = { defaultDukanDetailsResponse() })

        // Act
        val details = repo.getDukanDetailsByDukanId(dukanId)

        // Assert
        assertEquals(Dukan.Style.WIDE_IMAGE, details.style)
    }

    @Test
    fun `getDukanDetailsByDukanId should map latitude correctly`() = runTest {
        // Arrange
        val dukanId = "dukan123"
        val repo = createDukanRepository(dukanDetailsResponse = { defaultDukanDetailsResponse() })

        // Act
        val details = repo.getDukanDetailsByDukanId(dukanId)

        // Assert
        assertEquals(30.0444, details.coordinates.latitude)
    }

}