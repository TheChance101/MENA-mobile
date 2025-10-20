package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import net.thechance.mena.trends.data.dto.UserDto
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.data.util.NetworkConstants.USER_INFO_ENDPOINT
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.User
import net.thechance.mena.trends.domain.repository.UserRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single(binds = [UserRepository::class])
internal class UserRepositoryImpl(
    @Provided private val networkClient: HttpClient
) : UserRepository {

    override suspend fun getCurrentUserInfo(): User {
        return safeApiCall<UserDto> {
            networkClient.get(USER_INFO_ENDPOINT)
        }.toEntity()
    }
}