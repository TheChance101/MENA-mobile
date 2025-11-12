package net.thechance.mena.admin_panel.domin.service

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
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
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.service.AuthenticationService
import net.thechance.mena.admin_panel.data.utils.putAccessToken
import net.thechance.mena.admin_panel.data.utils.putRefreshToken
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSettingsApi::class)
class AuthenticationServiceTest {

    private lateinit var authenticationApiService: AuthenticationApiService
    private lateinit var settings: FlowSettings
    private lateinit var authenticationService: AuthenticationService

    @BeforeTest
    fun setup() {
        authenticationApiService = mock(mode = MockMode.autofill)
        settings = mock(mode = MockMode.autofill)
        authenticationService = AuthenticationService(authenticationApiService, settings)
    }

    @Test
    fun `refreshAccessToken should save tokens and return access token`() = runTest {
        everySuspend {
            settings.getStringFlow(REFRESH_TOKEN_KEY, "")
        } returns flowOf(fakeResponse.refreshToken)
        everySuspend {
            settings.getStringFlow(ACCESS_TOKEN_KEY, "")
        } returns flowOf(fakeResponse.accessToken)

        everySuspend {
            authenticationApiService.refreshAccessToken(any())
        } returns successfulResponse()

        authenticationService.refreshAccessToken()

        verifySuspend { settings.putAccessToken(fakeResponse.accessToken) }
        verifySuspend { settings.putRefreshToken(fakeResponse.refreshToken) }
    }

    @Test
    fun `access token should be stored in settings after successful refreshAccessToken`() =
        runTest {
            everySuspend {
                settings.getStringFlow(REFRESH_TOKEN_KEY, "")
            } returns flowOf(fakeResponse.refreshToken)
            everySuspend {
                settings.getStringFlow(ACCESS_TOKEN_KEY, "")
            } returns flowOf(fakeResponse.accessToken)
            everySuspend {
                authenticationApiService.refreshAccessToken(any())
            } returns successfulResponse()

            authenticationService.refreshAccessToken()

            verifySuspend { settings.putAccessToken(fakeResponse.accessToken) }
        }

    @Test
    fun `refreshAccessToken should throw UnauthorizedException on 401`() = runTest {
        everySuspend {
            settings.getStringFlow(REFRESH_TOKEN_KEY, "")
        } returns flowOf(emptyResponse.refreshToken)
        everySuspend {
            settings.getStringFlow(ACCESS_TOKEN_KEY, "")
        } returns flowOf(emptyResponse.accessToken)
        everySuspend {
            authenticationApiService.refreshAccessToken(any())
        } returns unauthorizedResponse()

        authenticationService.refreshAccessToken()

        verifySuspend { settings.putAccessToken(emptyResponse.accessToken) }
        verifySuspend { settings.putRefreshToken(emptyResponse.refreshToken) }
    }

    @Test
    fun `refreshAccessToken should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            settings.getStringFlow(REFRESH_TOKEN_KEY, "")
        } returns flowOf(fakeResponse.refreshToken)
        everySuspend {
            settings.getStringFlow(ACCESS_TOKEN_KEY, "")
        } returns flowOf(fakeResponse.accessToken)
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
        everySuspend {
            settings.getStringFlow(ACCESS_TOKEN_KEY, "")
        } returns flowOf("stored_access_token")

        val result = authenticationService.getAccessToken()

        assertEquals("stored_access_token", result)
    }

    @Test
    fun `getAccessToken should return empty string when no token stored`() = runTest {
        everySuspend {
            settings.getStringFlow(ACCESS_TOKEN_KEY, "")
        } returns flowOf("")

        val result = authenticationService.getAccessToken()

        assertEquals("", result)
    }

    private companion object {
        val fakeResponse = AdminAuthenticationResponse(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        val emptyResponse = AdminAuthenticationResponse(
            accessToken = "",
            refreshToken = ""
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