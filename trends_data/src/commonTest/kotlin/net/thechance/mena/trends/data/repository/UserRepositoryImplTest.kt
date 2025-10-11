package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.UserInfoDto
import net.thechance.mena.trends.data.repository.util.mockUserInfoHttpClient
import net.thechance.mena.trends.domain.entity.User
import net.thechance.mena.trends.domain.repository.UserRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepository
    private lateinit var networkClient: NetworkClient

    @BeforeTest
    fun setup() {
        networkClient = mock<NetworkClient>()
        repository = UserRepositoryImpl(networkClient)
    }

    @Test
    fun `should return profile entity successfully when network client returns valid profile response`() =
        runTest {
            val testNetworkClient = mockUserInfoHttpClient(userInfoDto)
            repository = UserRepositoryImpl(testNetworkClient)

            val actualProfile = repository.getCurrentUserInfo()

            assertThat(actualProfile).isEqualTo(expectedUser)
        }
    private companion object {
        val expectedUser = User(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )
        val userInfoDto = UserInfoDto(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )
    }
}