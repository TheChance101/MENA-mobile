package net.thechance.mena.admin_panel.data.repository

import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.api_service.AdminAuthenticationApiService
import net.thechance.mena.admin_panel.data.repository.authentication.AdminAuthenticationRepositoryImpl
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AdminAuthenticationRepositoryImplTest {
    private lateinit var adminAuthenticationApiService: AdminAuthenticationApiService
    private lateinit var settings: Settings

    private lateinit var adminAuthenticationRepositoryImpl: AdminAuthenticationRepositoryImpl

    @BeforeTest
    fun setup() {
        adminAuthenticationApiService =
            mock<AdminAuthenticationApiService>(mode = MockMode.autofill)
        settings = mock<Settings>(mode = MockMode.autofill)
        adminAuthenticationRepositoryImpl =
            AdminAuthenticationRepositoryImpl(
                adminAuthenticationApiService = adminAuthenticationApiService,
                settings = settings
            )
    }

    @Test
    fun `login should save access token on success`() = runTest {

        everySuspend {
            adminAuthenticationApiService.login(any())
        } returns successfulLoginResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)

        verify { settings.accessToken = fakeResponse.accessToken }
    }

    @Test
    fun `login should save refresh token on success`() = runTest {

        everySuspend {
            adminAuthenticationApiService.login(any())
        } returns successfulLoginResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)

        verify { settings.refreshToken = fakeResponse.refreshToken }
    }

    @Test
    fun `login should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            adminAuthenticationApiService.login(any())
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
            adminAuthenticationApiService.login(any())
        } throws _root_ide_package_.kotlinx.io.IOException("No internet")

        assertFailsWith<NoInternetException> {
            adminAuthenticationRepositoryImpl
                .login(TEST_USERNAME, TEST_PASSWORD)
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
    }
}
