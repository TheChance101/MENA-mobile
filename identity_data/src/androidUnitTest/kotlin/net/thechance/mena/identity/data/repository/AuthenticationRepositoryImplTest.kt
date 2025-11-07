package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dataSource.local.setting.ACCESS_TOKEN
import net.thechance.mena.identity.data.dataSource.local.setting.IMAGE_UPLOAD_COMPLETED
import net.thechance.mena.identity.data.dataSource.local.setting.LAST_REGISTRATION_PHONE_NUMBER
import net.thechance.mena.identity.data.dataSource.local.setting.REFRESH_TOKEN
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class AuthenticationRepositoryImplTest {

    private val client: HttpClient = mockk(relaxed = true)
    private val settings: Settings = mockk(relaxed = true)

    private var authenticationRepository: AuthenticationRepositoryImpl =
        AuthenticationRepositoryImpl(client, settings)


    @Test
    fun `refresh token should be stored in settings after successful login`() = runTest {

        val client = mockHttpClient(fakeLoginResponse)

        authenticationRepository = AuthenticationRepositoryImpl(client = client, settings)

        authenticationRepository.login(PhoneNumber("20", "1234567890"), "password123")

        verify { settings.putString(REFRESH_TOKEN, "fake_refresh_token") }
    }

    @Test
    fun `access token should be stored in settings after successful login`() = runTest {

        val client = mockHttpClient(fakeLoginResponse)

        authenticationRepository = AuthenticationRepositoryImpl(client = client, settings)

        authenticationRepository.login(PhoneNumber("20", "1234567890"), "password123")

        verify { settings.putString(ACCESS_TOKEN, "fake_access_token") }
    }

    @Test
    fun `login() should throw UnAuthorizedException when server returns 401`() = runTest {

        val client = mockHttpClientError(HttpStatusCode.Unauthorized)
        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        assertFailure {
            authenticationRepository.login(PhoneNumber("20", "1234567890"), "wrongPassword")
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `login() should throw InvalidCredentialsException when server returns 404`() = runTest {

        val client = mockHttpClientError(HttpStatusCode.NotFound)
        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        assertFailure {
            authenticationRepository.login(PhoneNumber("20", "12345670"), "Password123")
        }.isInstanceOf<InvalidCredentialsException>()

    }

    @Test
    fun `login() should throw UserIsBlockedException when server returns 403`() = runTest {

        val client = mockHttpClientError(HttpStatusCode.Forbidden)
        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        assertFailure {
            authenticationRepository.login(PhoneNumber("+20", "01234567"), "password123")
        }.isInstanceOf<UserIsBlockedException>()
    }

    @Test
    fun `refresh token should be stored in settings after successful refreshAccessTokens`() =
        runTest {

            val client = mockHttpClient(fakeLoginResponse)

            authenticationRepository = AuthenticationRepositoryImpl(client = client, settings)

            authenticationRepository.refreshAccessToken()

            verify { settings.putString(REFRESH_TOKEN, "fake_refresh_token") }
        }

    @Test
    fun `access token should be stored in settings after successful refreshAccessTokens`() =
        runTest {

            val client = mockHttpClient(fakeLoginResponse)

            every { settings.getString(ACCESS_TOKEN, "") } returns fakeLoginResponse.accessToken

            authenticationRepository = AuthenticationRepositoryImpl(client = client, settings)

            authenticationRepository.refreshAccessToken()

            verify { settings.putString(ACCESS_TOKEN, "fake_access_token") }
        }

    @Test
    fun `refreshAccessToken() should return stored access token`() = runTest {
        val client = mockHttpClient(fakeLoginResponse)
        every { settings.getString(ACCESS_TOKEN, "") } returns fakeLoginResponse.accessToken
        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        val actual = authenticationRepository.refreshAccessToken()

        assertEquals(fakeLoginResponse.accessToken, actual)
    }

    @Test
    fun `refreshAccessToken() should throw UnAuthorizedException when server returns 401`() =
        runTest {

            val client = mockHttpClientError(HttpStatusCode.Unauthorized)
            authenticationRepository = AuthenticationRepositoryImpl(client, settings)

            assertFailure {
                authenticationRepository.refreshAccessToken()
            }.isInstanceOf<UnAuthorizedException>()
        }

    @Test
    fun `getAccessToken() should return stored token from settings`() = runTest {

        val expectedToken = fakeLoginResponse.accessToken
        every { settings.getString(ACCESS_TOKEN, "") } returns expectedToken


        val result = authenticationRepository.getAccessToken()

        assertThat(result).isEqualTo(expectedToken)
    }

    @Test
    fun `getAccessToken() should return empty string when no token stored`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns ""

        val result = authenticationRepository.getAccessToken()

        assertThat(result).isEmpty()
    }

    @Test
    fun `observeToken emits new value when login is called`() = runTest {
        val client = mockHttpClient(fakeLoginResponse)

        authenticationRepository = AuthenticationRepositoryImpl(client = client, settings)

        authenticationRepository.login(PhoneNumber("20", "1234567890"), "password123")

        val result = authenticationRepository.observeTokenChange()

        assertEquals(fakeLoginResponse.accessToken, result.value)
    }

    @Test
    fun `saveAuthTokensWithoutEmit should save tokens without emitting to StateFlow`() = runTest {
        val authTokens = net.thechance.mena.identity.domain.model.AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        authenticationRepository.saveAuthTokensWithoutEmit(authTokens)

        verify { settings.putString(ACCESS_TOKEN, "test_access_token") }
    }

    @Test
    fun `saveAuthTokensWithoutEmit should save refresh token`() = runTest {
        val authTokens = net.thechance.mena.identity.domain.model.AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        authenticationRepository.saveAuthTokensWithoutEmit(authTokens)

        verify { settings.putString(REFRESH_TOKEN, "test_refresh_token") }
    }

    @Test
    fun `saveAuthTokensAndEmit should save tokens and emit to StateFlow`() = runTest {
        val authTokens = net.thechance.mena.identity.domain.model.AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        authenticationRepository.saveAuthTokensAndEmit(authTokens)

        verify { settings.putString(ACCESS_TOKEN, "test_access_token") }
    }

    @Test
    fun `saveAuthTokensAndEmit should emit token to StateFlow`() = runTest {
        val authTokens = net.thechance.mena.identity.domain.model.AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        authenticationRepository.saveAuthTokensAndEmit(authTokens)

        val result = authenticationRepository.observeTokenChange()
        assertEquals("test_access_token", result.value)
    }

    @Test
    fun `clearAuthTokens should clear access token`() = runTest {
        authenticationRepository.clearAuthTokens()

        verify { settings.putString(ACCESS_TOKEN, "") }
    }

    @Test
    fun `clearAuthTokens should clear refresh token`() = runTest {
        authenticationRepository.clearAuthTokens()

        verify { settings.putString(REFRESH_TOKEN, "") }
    }

    @Test
    fun `getAuthTokens should return AuthenticationTokens when both tokens exist`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "test_access_token"
        every { settings.getString(REFRESH_TOKEN, "") } returns "test_refresh_token"

        val result = authenticationRepository.getAuthTokens()

        assertEquals("test_access_token", result?.accessToken)
    }

    @Test
    fun `getAuthTokens should return AuthenticationTokens with correct refresh token`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "test_access_token"
        every { settings.getString(REFRESH_TOKEN, "") } returns "test_refresh_token"

        val result = authenticationRepository.getAuthTokens()

        assertEquals("test_refresh_token", result?.refreshToken)
    }

    @Test
    fun `getAuthTokens should return null when access token is blank`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns ""
        every { settings.getString(REFRESH_TOKEN, "") } returns "test_refresh_token"

        val result = authenticationRepository.getAuthTokens()

        assertNull(result)
    }

    @Test
    fun `getAuthTokens should return null when refresh token is blank`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "test_access_token"
        every { settings.getString(REFRESH_TOKEN, "") } returns ""

        val result = authenticationRepository.getAuthTokens()

        assertNull(result)
    }

    @Test
    fun `getAuthTokens should return null when both tokens are blank`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns ""
        every { settings.getString(REFRESH_TOKEN, "") } returns ""

        val result = authenticationRepository.getAuthTokens()

        assertNull(result)
    }

    @Test
    fun `observeTokenChange should initialize with access token when lastPhoneNumber is blank`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "initial_token"
        every { settings.getString(LAST_REGISTRATION_PHONE_NUMBER, "") } returns ""
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns false

        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        val result = authenticationRepository.observeTokenChange()

        assertEquals("initial_token", result.value)
    }

    @Test
    fun `observeTokenChange should initialize with access token when imageUploadCompleted is true`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "initial_token"
        every { settings.getString(LAST_REGISTRATION_PHONE_NUMBER, "") } returns "+9647901234567"
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns true

        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        val result = authenticationRepository.observeTokenChange()

        assertEquals("initial_token", result.value)
    }

    @Test
    fun `observeTokenChange should initialize with empty string when lastPhoneNumber exists and imageUploadCompleted is false`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns "initial_token"
        every { settings.getString(LAST_REGISTRATION_PHONE_NUMBER, "") } returns "+9647901234567"
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns false

        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        val result = authenticationRepository.observeTokenChange()

        assertEquals("", result.value)
    }

    @Test
    fun `observeTokenChange should initialize with empty string when no access token exists`() = runTest {
        every { settings.getString(ACCESS_TOKEN, "") } returns ""
        every { settings.getString(LAST_REGISTRATION_PHONE_NUMBER, "") } returns ""
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns false

        authenticationRepository = AuthenticationRepositoryImpl(client, settings)

        val result = authenticationRepository.observeTokenChange()

        assertEquals("", result.value)
    }

    @Test
    fun `saveAuthTokensWithoutEmit should not emit token to StateFlow`() = runTest {
        val authTokens = AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        authenticationRepository.saveAuthTokensWithoutEmit(authTokens)

        val result = authenticationRepository.observeTokenChange()
        assertEquals("", result.value)
    }

    @Test
    fun `refreshAccessToken should not emit token when in temporary token mode`() = runTest {
        val client = mockHttpClient(fakeLoginResponse)
        every { settings.getString(REFRESH_TOKEN, "") } returns "old_refresh_token"
        every { settings.getString(ACCESS_TOKEN, "") } returns "old_access_token"
        every { settings.getString(LAST_REGISTRATION_PHONE_NUMBER, "") } returns "+9647901234567"
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns false

        authenticationRepository = AuthenticationRepositoryImpl(client, settings)
        val initialValue = authenticationRepository.observeTokenChange().value
        authenticationRepository.saveAuthTokensWithoutEmit(
            AuthenticationTokens(
                accessToken = "temp_access_token",
                refreshToken = "temp_refresh_token"
            )
        )

        authenticationRepository.refreshAccessToken()

        val result = authenticationRepository.observeTokenChange()
        assertEquals(initialValue, result.value)
    }

}

val fakeLoginResponse = AuthenticationResponse(
    accessToken = "fake_access_token",
    refreshToken = "fake_refresh_token"
)
