package net.thechance.mena.admin_panel.data.repository

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
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.service.AdminPanelApiService
import net.thechance.mena.admin_panel.data.repository.authentication.AdminAuthenticationRepositoryImpl
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AdminAuthenticationRepositoryImplTest {
    private lateinit var adminPanelApiService: AdminPanelApiService
    private lateinit var settings: Settings

    private lateinit var adminAuthenticationRepositoryImpl: AdminAuthenticationRepositoryImpl

    @BeforeTest
    fun setup() {
        adminPanelApiService =
            mock<AdminPanelApiService>(mode = MockMode.autofill)
        settings = mock<Settings>(mode = MockMode.autofill)
        adminAuthenticationRepositoryImpl =
            AdminAuthenticationRepositoryImpl(
                adminPanelApiService = adminPanelApiService,
                settings = settings
            )
    }

    @Test
    fun `login should save access token on success`() = runTest {

        everySuspend {
            adminPanelApiService.login(any())
        } returns successfulResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)

        verify { settings.accessToken = fakeResponse.accessToken }
    }

    @Test
    fun `login should save refresh token on success`() = runTest {

        everySuspend {
            adminPanelApiService.login(any())
        } returns successfulResponse()

        adminAuthenticationRepositoryImpl.login(TEST_USERNAME, TEST_PASSWORD)

        verify { settings.refreshToken = fakeResponse.refreshToken }
    }

    @Test
    fun `login should throw UnknownNetworkException on 401 Unauthorized`() = runTest {
        everySuspend {
            adminPanelApiService.login(any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnknownNetworkException> {
            adminAuthenticationRepositoryImpl
                .login(TEST_USERNAME, TEST_PASSWORD)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `login should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            adminPanelApiService.login(any())
        } throws _root_ide_package_.kotlinx.io.IOException("No internet")

        assertFailsWith<NoInternetException> {
            adminAuthenticationRepositoryImpl
                .login(TEST_USERNAME, TEST_PASSWORD)
        }
    }

    @Test
    fun `refreshAccessToken should save tokens and return access token on success`() = runTest {
        everySuspend {
            adminPanelApiService.refreshAccessToken(any())
        } returns successfulResponse()

        adminAuthenticationRepositoryImpl.refreshAccessToken()

        verify { settings.accessToken = fakeResponse.accessToken }
        verify { settings.refreshToken = fakeResponse.refreshToken }
    }

    @Test
    fun `access token should be stored in settings after successful refreshAccessToken`() =
        runTest {
            everySuspend {
                adminPanelApiService.refreshAccessToken(any())
            } returns successfulResponse()

            adminAuthenticationRepositoryImpl.refreshAccessToken()

            verify { settings.accessToken = fakeResponse.accessToken }
        }

    @Test
    fun `refreshAccessToken should throw UnknownNetworkException on 401 Unauthorized`() = runTest {
        everySuspend {
            adminPanelApiService.refreshAccessToken(any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnknownNetworkException> {
            adminAuthenticationRepositoryImpl.refreshAccessToken()
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `refreshAccessToken should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            adminPanelApiService.refreshAccessToken(any())
        } throws _root_ide_package_.kotlinx.io.IOException(
            "No internet"
        )

        assertFailsWith<NoInternetException> {
            adminAuthenticationRepositoryImpl.refreshAccessToken()
        }
    }

    @Test
    fun `getAccessToken should return stored access token`() = runTest {
        every {
            settings.getString(ACCESS_TOKEN_KEY, "")
        } returns "stored_access_token"

        val result = adminAuthenticationRepositoryImpl.getAccessToken()

        assertEquals("stored_access_token", result)
    }

    @Test
    fun `getAccessToken should return empty string when no token stored`() = runTest {
        every { settings.getString(ACCESS_TOKEN_KEY, "") } returns ""

        val result = adminAuthenticationRepositoryImpl.getAccessToken()

        assertEquals("", result)
    }


    private companion object {
        val fakeResponse = AdminAuthenticationResponse(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )
        const val TEST_USERNAME = "testUser"
        const val TEST_PASSWORD = "testPassword"
        const val ACCESS_TOKEN_KEY = "access_token"

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
    }
}
