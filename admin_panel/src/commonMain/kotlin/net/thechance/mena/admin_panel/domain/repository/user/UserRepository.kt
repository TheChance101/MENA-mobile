package net.thechance.mena.admin_panel.domain.repository.user

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQueryParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface UserRepository {
    suspend fun getUsers(userQueryParams: UserQueryParams?): List<User>
    suspend fun updateUserStatus(userID: Uuid, status: Status)
}