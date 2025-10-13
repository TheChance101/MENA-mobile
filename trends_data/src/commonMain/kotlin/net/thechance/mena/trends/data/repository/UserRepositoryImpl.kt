package net.thechance.mena.trends.data.repository

import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.UserInfoDto
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.IDENTITY_PATH
import net.thechance.mena.trends.data.util.NetworkConstants.PROFILE_ENDPOINT
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.User
import net.thechance.mena.trends.domain.repository.UserRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [UserRepository::class])
internal class UserRepositoryImpl(
    @Provided private val networkClient: NetworkClient
) : UserRepository {

    override suspend fun getCurrentUserInfo(): User {
        return safeApiCall<UserInfoDto> {
            networkClient.get("/$IDENTITY_PATH/$PROFILE_ENDPOINT")
        }.toEntity()
    }
}