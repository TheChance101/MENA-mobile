package net.thechance.mena.core_chat.data.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.config
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.WebSocketCapability
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.remote.network.createHttpClient
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.service.AuthorizationService
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(InternalAPI::class)
class CreateHttpClientTest {

    private val mockEngine = mock<HttpClientEngine>()
    private lateinit var mockFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    private val authenticationRepository: AuthenticationRepository = mock(MockMode.autofill)
    private val authorizationService: AuthorizationService =
        AuthorizationService(authenticationRepository)
    private val json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    @BeforeTest
    fun setUp() {
        every { mockEngine.coroutineContext } returns EmptyCoroutineContext
        every { mockEngine.dispatcher } returns Dispatchers.IO
        every { mockEngine.close() } returns Unit
        every { mockEngine.config } returns HttpClientEngineConfig()
        every { mockEngine.install(any()) } returns Unit

        every { mockEngine.supportedCapabilities } returns setOf(
            HttpTimeoutCapability,
            WebSocketCapability
        )

        mockFactory = mock {
            every { create(any()) } returns mockEngine
        }

    }

    @Test
    fun `createHttpClient should return non null HttpClient when factory is provided`() {
        val client =
            createHttpClient("https://example.com", authorizationService, mockFactory, json)
        assertThat(client).isNotNull()
    }

    @Test
    fun `createHttpClient should delegate engine creation to factory`() {
        createHttpClient("https://example.com", authorizationService, mockFactory, json)
        verify { mockFactory.create(any()) }
    }

    @Test
    fun `createHttpClient should install ContentNegotiation plugin`() {
        val client =
            createHttpClient("https://example.com", authorizationService, mockFactory, json)
        assertThat(client.plugin(ContentNegotiation)).isNotNull()
    }

    @Test
    fun `createHttpClient should install Logging plugin`() {
        val client =
            createHttpClient("https://example.com", authorizationService, mockFactory, json)
        assertThat(client.plugin(Logging)).isNotNull()
    }

    @Test
    fun `createHttpClient should set baseUrl in default request`() = runTest {
        var baseUrl: Url? = null

        val engineFactory = MockEngine.config {
            addHandler { request ->
                baseUrl = request.url
                respondOk()
            }
        }

        val client =
            createHttpClient("https://example.com", authorizationService, engineFactory, json)

        client.get("/test")

        assertThat(baseUrl.toString()).isEqualTo("https://example.com/test")
    }

    @Test
    fun `createHttpClient should set default ContentType to application json`() = runTest {
        var contentType: ContentType? = null

        val engineFactory = MockEngine.config {
            addHandler { request ->
                contentType = request.body.contentType
                respondOk()
            }
        }

        val client =
            createHttpClient("https://example.com", authorizationService, engineFactory, json)

        client.post("/test") { setBody("{}") }

        assertThat(contentType).isEqualTo(ContentType.Application.Json)
    }


    @Test
    fun `createHttpClient should set Accept header to application json`() = runTest {
        var acceptHeader: String? = null

        val engineFactory = MockEngine.config {
            addHandler { request ->
                acceptHeader = request.headers[HttpHeaders.Accept]
                respondOk()
            }
        }

        val client =
            createHttpClient("https://example.com", authorizationService, engineFactory, json)

        client.get("/test")

        assertThat(acceptHeader).isEqualTo(ContentType.Application.Json.toString())
    }

}
