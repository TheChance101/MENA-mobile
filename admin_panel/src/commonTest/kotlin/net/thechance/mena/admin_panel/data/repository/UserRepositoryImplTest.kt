package net.thechance.mena.admin_panel.data.repository

import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.data.remote.api_service.UserApiService
import net.thechance.mena.admin_panel.data.repository.user.UserRepositoryImpl
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType
import net.thechance.mena.admin_panel.domain.model.UserQueryParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepositoryImplTest {

    private lateinit var userApiService: UserApiService
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeTest
    fun setup() {
        userApiService = mock<UserApiService>(mode = MockMode.autofill)
        userRepository = UserRepositoryImpl(userApiService)
    }

    @Test
    fun `getUsers should return mapped user list on success`() = runTest {
        val fakeUser = UserResponse(
            id = "123e4567-e89b-12d3-a456-426614174000",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "+201001234567",
            lastLoginAt = "2025-10-31",
            lastVisitAt = "2025-10-31",
            status = "ACTIVE"
        )

        val fakePagedResponse = PagedResponse(
            totalElements = 1,
            page = 0,
            pageSize = 10,
            totalPages = 1,
            items = listOf(fakeUser)
        )

        everySuspend {
            userApiService.getUsers(any(), any(), any(), any())
        } returns successfulResponse(fakePagedResponse)

        val result = userRepository.getUsers(
            UserQueryParams(
                searchInput = "Test",
                sortType = SortType.USERNAME,
                sortDirection = SortDirection.ASC,
                page = 0,
                size = 10
            )
        )

        assertEquals(1, result.items.size)
        assertEquals("Test", result.items.first().firstName)
    }


    @Test
    fun `getUsers should throw NoInternetException on IOException`() = runTest {
        everySuspend {
            userApiService.getUsers(any(), any(), any(), any())
        } throws IOException("No internet")

        assertFailsWith<NoInternetException> {
            userRepository.getUsers(null).items.first()
        }
    }

    @Test
    fun `getUsers should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            userApiService.getUsers(any(), any(), any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            userRepository.getUsers(null).items.first()
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `updateUserStatus should call api with correct parameters`() = runTest {
        val fakeUuid = Uuid.random()
        val status = User.Status.ACTIVE

        everySuspend {
            userApiService.updateUserStatus(any(), any())
        } returns successfulEmptyResponse()

        userRepository.updateUserStatus(fakeUuid, status)
    }

    @OptIn(InternalAPI::class)
    private fun <T> successfulResponse(body: T): Response<T> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }
        return Response.success(body = body, rawResponse = mockHttpResponse) as Response<T>
    }

    @OptIn(InternalAPI::class)
    private fun successfulEmptyResponse(): Response<Unit> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }
        return Response.success(Unit, mockHttpResponse) as Response<Unit>
    }

    @OptIn(InternalAPI::class)
    private fun <T> unauthorizedResponse(): Response<T> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.Unauthorized
        }
        return Response.error<T>(
            body = """{"message":"Unauthorized"}""",
            rawResponse = mockHttpResponse
        ) as Response<T>
    }
}
