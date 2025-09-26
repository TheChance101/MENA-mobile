package net.thechance.mena.identity.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.LoginResponseDto
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthRemoteDataSourceImplTest {

    private val client: HttpClient = mockk(relaxed = true)
    private lateinit var remoteDataSource: AuthRemoteDataSourceImpl

    @BeforeTest
    fun setUp() {
        remoteDataSource = AuthRemoteDataSourceImpl(client)
    }


    @Test
    fun `login should return response when status is 200`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Json.encodeToString(fakeLoginResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType, ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        remoteDataSource = AuthRemoteDataSourceImpl(client)
        // When
        val actual = remoteDataSource.login(LoginRequestDto("110061617", "12345678"))

        // Then
        assertEquals(fakeLoginResponse, actual)
    }

    @Test
    fun `login should throw ClientRequestException when status is not 200`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Unauthorized",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(
                    HttpHeaders.ContentType, ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        remoteDataSource = AuthRemoteDataSourceImpl(client)
        //When & Then
        assertFailsWith<ClientRequestException> {
            remoteDataSource.login(LoginRequestDto("110061617", "12345678"))
        }
    }

    @Test
    fun `refresh token should return response when status is 200`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Json.encodeToString(fakeLoginResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType, ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine){
            install(ContentNegotiation){ json() }
        }
        remoteDataSource = AuthRemoteDataSourceImpl(client)
        //When
        val actual  = remoteDataSource.refreshToken(RefreshRequestDto("fake_refresh_token"))
        //Then
        assertEquals(fakeLoginResponse , actual)
    }

    @Test
    fun `refresh token should throw ClientRequestException when status is not 200`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Unauthorized",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(
                    HttpHeaders.ContentType, ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        remoteDataSource = AuthRemoteDataSourceImpl(client)
        //When & Then
        assertFailsWith<ClientRequestException> {
            remoteDataSource.refreshToken(RefreshRequestDto("fake_refresh_token"))
        }
    }
    companion object {
        private val fakeLoginResponse = LoginResponseDto(
            accessToken = "fake_access_token", refreshToken = "fake_refresh_token"
        )

    }
}
