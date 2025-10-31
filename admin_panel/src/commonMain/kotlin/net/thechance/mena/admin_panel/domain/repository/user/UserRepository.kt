package net.thechance.mena.admin_panel.domain.repository.user


import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserParams

interface UserRepository {
    suspend fun getUsers(userParams: UserParams?):List<User>
}