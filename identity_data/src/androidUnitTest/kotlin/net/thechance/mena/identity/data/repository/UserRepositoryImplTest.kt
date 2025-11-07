package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.response.ChangePasswordResponseDto
import net.thechance.mena.identity.data.dto.profile.response.ProfileResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class
UserRepositoryImplTest {

    private val client = mockk<HttpClient>()
    private val userDao = mockk<UserDao>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private var userRepositoryImpl = UserRepositoryImpl(
        client, userDao, testDispatcher,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUser() should return user stored in local database`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        coEvery { userDao.upsert(fakeProfileResponse.toDomain().toEntity()) } returns Unit
        every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

        val result = userRepositoryImpl.getUser()

        assertEquals(result.first(), fakeProfileResponse.toDomain())
    }

    @Test
    fun `getUser() should return null when there is no user stored`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        coEvery { userDao.upsert(fakeProfileResponse.toDomain().toEntity()) } returns Unit
        every { userDao.getUser() } returns flowOf(null)

        val result = userRepositoryImpl.getUser()

        assertEquals(result.first(), null)
    }

    @Test
    fun `getUser() should return user from local database when remote throws exception`() =
        runTest {

            val client = mockHttpClientError(HttpStatusCode.Unauthorized)
            userRepositoryImpl = UserRepositoryImpl(client, userDao  )

            every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

            val result = userRepositoryImpl.getUser()

            assertEquals(fakeProfileResponse.toDomain(), result.first())

        }

    @Test
    fun `getUser() should not call saveUserInfo when remote throws exception`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.Unauthorized)
            userRepositoryImpl = UserRepositoryImpl(client, userDao )

            every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

            userRepositoryImpl.getUser().first()

            coVerify(exactly = 0) { userDao.upsert(any()) }

        }

    @Test
    fun `getUser() should return empty flow when local database is empty`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao )

        coEvery { userDao.upsert(fakeProfileResponse.toDomain().toEntity()) } returns Unit
        every { userDao.getUser() } returns emptyFlow()

        val result = userRepositoryImpl.getUser()

        assertTrue(result.toList().isEmpty())
    }

    @Test
    fun `getUser() should return object from User`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao )

        coEvery { userDao.upsert(any()) } returns Unit
        every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

        val result = userRepositoryImpl.getUser()

        assertEquals(fakeProfileResponse.firstName, result.first()?.firstName)
        assertEquals(fakeProfileResponse.username, result.first()?.username)
        assertEquals(fakeProfileResponse.lastName, result.first()?.lastName)
        assertEquals(fakeProfileResponse.imageUrl, result.first()?.profileImageUrl)

    }

    @Test
    fun `updateUser() should call upsert user when try to update user`() = runTest {
        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao )
        userRepositoryImpl.updateUser(fakeUser)
        coVerify { userDao.upsert(any()) }
    }

    @Test
    fun `changePassword() should  not throw exception when server return 200`() = runTest {
        val client = mockHttpClient(fakeChangePasswordResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)


            userRepositoryImpl.changePassword(
                currentPassword = "Abcd1234",
                newPassword = "12345678",
                confirmPassword = "12345678"
            )


    }

    @Test
    fun `changePassword() should throw UnAuthorizedException when server return 401`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        assertFailure {
            userRepositoryImpl.changePassword(
                currentPassword = "Abcd1234",
                newPassword = "12345678",
                confirmPassword = "12345678"
            )
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `changePassword() should throw InvalidRequestException when server return 400`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.BadRequest)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        assertFailure {
            userRepositoryImpl.changePassword(
                currentPassword = "Abcd1234",
                newPassword = "12345678",
                confirmPassword = "12345678"
            )
        }.isInstanceOf<InvalidRequestException>()
    }


    val fakeProfileResponse = ProfileResponseDto(
        id = "1bfbf5d8-145d-40e9-abae-8335df3f0a81",
        firstName = "The",
        lastName = "Chance",
        username = "the_chance",
        imageUrl = "",
        birthDate = "1999-01-01",
        gender = UserEntity.MALE,
    )
    val fakeChangePasswordResponse = ChangePasswordResponseDto(
        message = "Password changed successfully"
    )

    @OptIn(ExperimentalUuidApi::class)
    val fakeUser = User(
        id = Uuid.parse("1bfbf5d8-145d-40e9-abae-8335df3f0a81"),
        username = "the_chance",
        firstName = "The",
        lastName = "Chance",
        profileImageUrl = "http://image.com",
        birthDate = LocalDate(1900, 1, 1),
        gender = Gender.MALE,
    )
}