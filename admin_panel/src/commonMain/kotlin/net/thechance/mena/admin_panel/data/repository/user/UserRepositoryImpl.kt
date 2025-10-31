package net.thechance.mena.admin_panel.data.repository.user

import net.thechance.mena.admin_panel.data.mapper.toEntityList
import net.thechance.mena.admin_panel.data.mapper.user.buildSortQuery
import net.thechance.mena.admin_panel.data.mapper.user.toDomain
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.data.remote.service.AdminPanelApiService
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQuery
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(
    private val adminPanelApiService: AdminPanelApiService,
) : UserRepository {
    override suspend fun getUsers(userQuery: UserQuery?): List<User> {
        val sortParam = buildSortQuery(userQuery?.sortType, userQuery?.sortDirection)
        return executeApiSafely<PagedResponse<UserResponse>>{
            adminPanelApiService
                .getUsers(
                    query=userQuery?.searchInput,
                    sort = sortParam,
                    page = userQuery?.page,
                    size = userQuery?.size

                )
        }.toEntityList(UserResponse::toDomain)
    }

}
