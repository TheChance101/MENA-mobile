package net.thechance.mena.identity.data.repository

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
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dto.profile.ProfileResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)

class UserRepositoryImplTest {

    private val client = mockk<HttpClient>()
    private val userDao = mockk<UserDao>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private var userRepositoryImpl = UserRepositoryImpl(
        client, userDao, testDispatcher
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
    fun `getUser() should return user from local database when remote throws exception`() =
        runTest {

            val client = mockHttpClientError(HttpStatusCode.Unauthorized)
            userRepositoryImpl = UserRepositoryImpl(client, userDao)

            every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

            val result = userRepositoryImpl.getUser()

            assertEquals(fakeProfileResponse.toDomain(), result.first())

        }

    @Test
    fun `getUser() should not call saveUserInfo when remote throws exception`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.Unauthorized)
            userRepositoryImpl = UserRepositoryImpl(client, userDao)

            every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

            userRepositoryImpl.getUser().first()

            coVerify(exactly = 0) { userDao.upsert(any()) }

        }

    @Test
    fun `getUser() should return empty flow when local database is empty`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        coEvery { userDao.upsert(fakeProfileResponse.toDomain().toEntity()) } returns Unit
        every { userDao.getUser() } returns emptyFlow()

        val result = userRepositoryImpl.getUser()

        assertTrue(result.toList().isEmpty())
    }

    @Test
    fun `getUser() should return object from User`() = runTest {

        val client = mockHttpClient(fakeProfileResponse)
        userRepositoryImpl = UserRepositoryImpl(client, userDao)

        coEvery { userDao.upsert(any()) } returns Unit
        every { userDao.getUser() } returns flowOf(fakeProfileResponse.toDomain().toEntity())

        val result = userRepositoryImpl.getUser()

        assertEquals(fakeProfileResponse.firstName, result.first()?.firstName)
        assertEquals(fakeProfileResponse.username, result.first()?.username)
        assertEquals(fakeProfileResponse.lastName, result.first()?.lastName)
        assertEquals(fakeProfileResponse.profileImageUrl, result.first()?.profileImageUrl)

    }


    val fakeProfileResponse = ProfileResponseDto(
        firstName = "The",
        lastName = "Chance",
        username = "TheChance@test.com",
        profileImageUrl = ""
    )
}