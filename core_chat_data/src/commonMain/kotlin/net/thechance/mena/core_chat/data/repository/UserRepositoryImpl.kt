package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.repository.UserRepository

class UserRepositoryImpl(
    private val client: HttpClient,
    ): BaseRepository, UserRepository {
    override suspend fun getUserInfo(): User {
        return tryNetworkCall<UserDto>(
            bodyType = typeInfo<UserDto>()
        ){
            client.get(USER_ENDPOINT)
        }?.toDomain() ?: throw NotFoundException("User not found")
    }

    private companion object{
        const val USER_ENDPOINT = "/chat/user"
    }

}