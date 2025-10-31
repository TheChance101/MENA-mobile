package net.thechance.mena.admin_panel.domain.use_case.user

import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQuery
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
class UserUseCase (
    @Provided
    private val userRepo : UserRepository

){
    suspend fun getUsers(userQuery: UserQuery):List<User>
    {
        return userRepo.getUsers(userQuery)

    }
}