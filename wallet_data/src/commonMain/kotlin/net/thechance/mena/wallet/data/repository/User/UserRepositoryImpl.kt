package net.thechance.mena.wallet.data.repository.User

import net.thechance.mena.wallet.domain.entity.User
import net.thechance.mena.wallet.domain.repository.UserRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class UserRepositoryImpl : UserRepository{
    override suspend fun getUserById(id: Uuid): User {
        return User(
            id = id,
            name = "nour",
            imgUrl = null
        )
    }
}