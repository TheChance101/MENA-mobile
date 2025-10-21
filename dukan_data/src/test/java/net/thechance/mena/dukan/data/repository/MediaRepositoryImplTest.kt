package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.dukan.data.util.constants.EndPoints
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MediaRepositoryImplTest {

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

    @Test
    fun `uploadDukanImage SHOULD post to correct endpoint and return url`() = runTest {
        // Arrange
        val client = createMockClient()
        val repo = MediaRepositoryImpl(client)

        // Act
        val result = repo.uploadDukanImage("test.png", byteArrayOf(1, 2, 3))

        // Assert
        assertTrue(result.contains("https://mock/image.jpg"))
        assertTrue(capturedRequest.url.encodedPath.contains("${EndPoints.DUKAN_BASE_PATH}/image"))
        assertTrue(capturedRequest.body is MultiPartFormDataContent)
    }

    @Test
    fun `uploadProductImages SHOULD post to correct endpoint and return urls`() = runTest {
        // Arrange
        val client = createMockClient()
        val repo = MediaRepositoryImpl(client)

        // Act
        val result = repo.uploadProductImages(
            fileName = listOf("a.png", "b.png"),
            fileBytes = listOf(byteArrayOf(1), byteArrayOf(2)),
            productId = "product_1"
        )

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.first().contains("https://mock/image.jpg"))
        assertTrue(capturedRequest.url.encodedPath.contains("${EndPoints.PRODUCT_BASE_PATH}/images/product_1"))
        assertTrue(capturedRequest.body is MultiPartFormDataContent)
    }

    @Test
    fun `uploadProductImages SHOULD throw when lists size differ`() = runTest {
        val client = createMockClient()
        val repo = MediaRepositoryImpl(client)

        val exception = assertFailsWith<IllegalArgumentException> {
            repo.uploadProductImages(
                fileName = listOf("a.png"),
                fileBytes = listOf(byteArrayOf(1), byteArrayOf(2)),
                productId = "product_1"
            )
        }

        assertEquals("fileNames and fileBytes must have the same size.", exception.message)
    }
}