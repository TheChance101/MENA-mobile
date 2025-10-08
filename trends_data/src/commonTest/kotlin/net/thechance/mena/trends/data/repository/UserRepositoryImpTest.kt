package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.everySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.ProfileDto
import net.thechance.mena.trends.data.util.NetworkConstants
import net.thechance.mena.trends.domain.entity.Profile
import net.thechance.mena.trends.domain.repository.UserRepository
import kotlin.test.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepository
    private lateinit var networkClient: NetworkClient

    @Test
    fun `should return profile entity successfully when network client returns valid profile response`() = runTest {
        val expectedProfile = Profile(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )
        val profileDto = ProfileDto(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )

//        networkClient = mock<NetworkClient>()
        everySuspend { networkClient.get(NetworkConstants.PROFILE_ENDPOINT) }
        repository = UserRepositoryImpl(networkClient)

        val result = runCatching { repository.getCurrentUserProfile() }

        assertThat(result.getOrNull()).isEqualTo(expectedProfile)
    }


}