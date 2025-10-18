package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dto.profile.ProfileResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository


class UserRepositoryImpl(
    private val client: HttpClient,
    private val userDao: UserDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {
    override suspend fun getUser(): Flow<User?> {
        CoroutineScope(dispatcher).launch {
            try {
                val user: ProfileResponseDto = client.getJson(path = PROFILE)
                userDao.upsert(user.toEntity())
            } catch (_: Exception) {
            }
        }

        return userDao.getUser()
            .map { userEntity ->
                userEntity?.toDomain()
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun updateUser(user: User) {
        // todo add update user from server
        userDao.upsert(user.toEntity())
    }

    companion object {
        const val PROFILE = "identity/profile/me"
    }
}