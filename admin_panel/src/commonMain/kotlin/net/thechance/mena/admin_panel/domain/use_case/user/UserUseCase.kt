package net.thechance.mena.admin_panel.domain.use_case.user

import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQuery
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class UserUseCase(
    @Provided
    private val userRepository: UserRepository

) {
    suspend fun getUsers(userQuery: UserQuery): List<User> {
        return userRepository.getUsers(userQuery)

    }

    suspend fun updateUserStatus(userID: Uuid , status : Status) {
        return userRepository.updateUserStatus(userID ,status)

    }

}