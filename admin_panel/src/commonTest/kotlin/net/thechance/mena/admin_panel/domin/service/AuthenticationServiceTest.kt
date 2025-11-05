package net.thechance.mena.admin_panel.domin.service

import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.repository.authentication.AdminAuthenticationRepositoryImpl
import net.thechance.mena.admin_panel.data.service.AuthenticationService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthenticationServiceTest {

    private lateinit var authenticationApiService: AuthenticationApiService
    private lateinit var settings: Settings
    private lateinit var authenticationService: AuthenticationService
    private lateinit var authenticationRepository: AdminAuthenticationRepositoryImpl

    @BeforeTest
    fun setup() {
        authenticationApiService = mock(mode = MockMode.autofill)
        settings = mock(mode = MockMode.autofill)
        authenticationService = AuthenticationService(authenticationApiService, settings)
        authenticationRepository = AdminAuthenticationRepositoryImpl(authenticationApiService, settings)
    }

    @Test
    fun `refreshAccessToken should save tokens and return access token`() = runTest {
        everySuspend {
            authenticationApiService.refreshAccessToken(any())
        } returns successfulResponse()

         authenticationService.refreshAccessToken()

        verify { settings.accessToken = fakeResponse.accessToken }
        verify { settings.refreshToken = fakeResponse.refreshToken }
    }

    @Test
    fun `access token should be stored in settings after successful refreshAccessToken`() =
        runTest {
            everySuspend {
                authenticationApiService.refreshAccessToken(any())
            } returns successfulResponse()

            authenticationService.refreshAccessToken()

            verify { settings.accessToken = fakeResponse.accessToken }
        }

    @Test
    fun `refreshAccessToken should throw UnauthorizedException on 401`() = runTest {
        everySuspend {
            authenticationApiService.refreshAccessToken(any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            authenticationService.refreshAccessToken()
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `refreshAccessToken should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            authenticationApiService.refreshAccessToken(any())
        } throws _root_ide_package_.kotlinx.io.IOException(
            "No internet"
        )

        assertFailsWith<NoInternetException> {
            authenticationService.refreshAccessToken()
        }
    }

    @Test
    fun `getAccessToken should return stored access token`() = runTest {
        every {
            settings.getString(ACCESS_TOKEN_KEY, "")
        } returns "stored_access_token"

        val result = authenticationService.getAccessToken()

        assertEquals("stored_access_token", result)
    }

    @Test
    fun `getAccessToken should return empty string when no token stored`() = runTest {
        every { settings.getString(ACCESS_TOKEN_KEY, "") } returns ""

        val result = authenticationService.getAccessToken()

        assertEquals("", result)
    }

    private companion object {
        val fakeResponse = AdminAuthenticationResponse(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"

        @OptIn(InternalAPI::class)
        fun successfulResponse(): Response<AdminAuthenticationResponse> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.OK
            }
            return Response.success(
                body = fakeResponse,
                rawResponse = mockHttpResponse
            ) as Response<AdminAuthenticationResponse>
        }

        @OptIn(InternalAPI::class)
        fun unauthorizedResponse(): Response<AdminAuthenticationResponse> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.Unauthorized
            }
            return Response.error<AdminAuthenticationResponse>(
                body = """{"message":"Invalid credentials"}""",
                rawResponse = mockHttpResponse
            ) as Response<AdminAuthenticationResponse>
        }
        private fun successfulLogoutResponse(): Response<Unit> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.OK
            }
            return Response.success(
                body = Unit,
                rawResponse = mockHttpResponse
            ) as Response<Unit>
        }
    }
}