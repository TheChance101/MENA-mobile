package net.thechance.mena.admin_panel.domain.repository.user


import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQuery

interface UserRepository {
    suspend fun getUsers(userQuery: UserQuery?):List<User>
}