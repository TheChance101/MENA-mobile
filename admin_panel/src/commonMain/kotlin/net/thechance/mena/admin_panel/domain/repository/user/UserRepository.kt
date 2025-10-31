package net.thechance.mena.admin_panel.domain.repository.user


import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQuery
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
interface UserRepository {
    suspend fun getUsers(userQuery: UserQuery?):List<User>

    suspend fun updateUserStatus(userID: Uuid , status : Status)

}