package net.thechance.mena.core_chat.data.repository

import io.ktor.client.request.get
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.network.CustomHttpClient
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.repository.UserRepository

class UserRepositoryImpl(
    private val customHttpClient: CustomHttpClient,
) : UserRepository {

    private val client = customHttpClient.getClient()
    override suspend fun getUserInfo(): User {
        return tryNetworkCall<UserDto>(
            bodyType = typeInfo<UserDto>()
        ){
            client.get(USER_ENDPOINT)
        }.toDomain()
    }

    private companion object{
        const val USER_ENDPOINT = "/chat/user"
    }

}