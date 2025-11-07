package net.thechance.mena.admin_panel.data.repository

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
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.repository.authentication.AdminAuthenticationRepositoryImpl
import net.thechance.mena.admin_panel.data.utils.putAccessToken
import net.thechance.mena.admin_panel.data.utils.putRefreshToken
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSettingsApi::class)
class AdminAuthenticationRepositoryImplTest {
    private lateinit var authenticationApiService: AuthenticationApiService
    private lateinit var settings: FlowSettings

    private lateinit var adminAuthenticationRepositoryImpl: AdminAuthenticationRepositoryImpl

    @BeforeTest
    fun setup() {
        authenticationApiService =
            mock<AuthenticationApiService>(mode = MockMode.autofill)
        settings = mock<FlowSettings>(mode = MockMode.autofill)
        adminAuthenticationRepositoryImpl =
            AdminAuthenticationRepositoryImpl(
                authenticationApiService = authenticationApiService,
                settings = settings
            )
    }

    @Test
    fun `login should save access token on success`() = runTest {

        everySuspend {
            authenticationApiService.login(any())
        } returns successfulLoginResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)
        verifySuspend { settings.putAccessToken(fakeResponse.accessToken) }
    }

    @Test
    fun `login should save refresh token on success`() = runTest {

        everySuspend {
            authenticationApiService.login(any())
        } returns successfulLoginResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)

        verifySuspend { settings.putRefreshToken(fakeResponse.refreshToken) }
    }

    @Test
    fun `login should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            authenticationApiService.login(any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            adminAuthenticationRepositoryImpl
                .login(TEST_USERNAME, TEST_PASSWORD)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `login should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            authenticationApiService.login(any())
        } throws _root_ide_package_.kotlinx.io.IOException("No internet")

        assertFailsWith<NoInternetException> {
            adminAuthenticationRepositoryImpl
                .login(TEST_USERNAME, TEST_PASSWORD)
        }
    }

    @Test
    fun `logout should call api logout endpoint`() = runTest {
        everySuspend { authenticationApiService.logout() } returns successfulLogoutResponse()

        adminAuthenticationRepositoryImpl.logout()

        verifySuspend { authenticationApiService.logout() }
    }

    @Test
    fun `logout should clear access and refresh tokens on success`() = runTest {
        everySuspend { authenticationApiService.logout() } returns successfulLogoutResponse()

        adminAuthenticationRepositoryImpl.logout()

        verifySuspend { settings.putAccessToken("") }
        verifySuspend { settings.putRefreshToken("") }
    }

    @Test
    fun `logout should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            authenticationApiService.logout()
        } throws _root_ide_package_.kotlinx.io.IOException("No internet")

        assertFailsWith<NoInternetException> {
            adminAuthenticationRepositoryImpl.logout()
        }
    }

    private companion object {
        val fakeResponse = AdminAuthenticationResponse(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )
        const val TEST_USERNAME = "testUser"
        const val TEST_PASSWORD = "testPassword"

        @OptIn(InternalAPI::class)
        fun successfulLoginResponse(): Response<AdminAuthenticationResponse> {
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

        @OptIn(InternalAPI::class)
        fun successfulLogoutResponse(): Response<Unit> {
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
