package net.thechance.mena.admin_panel.data.repository.user

import net.thechance.mena.admin_panel.data.mapper.toEntityList
import net.thechance.mena.admin_panel.data.mapper.user.buildSortQuery
import net.thechance.mena.admin_panel.data.mapper.user.toEntity
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.data.remote.service.AdminPanelApiService
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQueryParams
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class UserRepositoryImpl(
    private val adminPanelApiService: AdminPanelApiService,
) : UserRepository {
    override suspend fun getUsers(userQuery: UserQueryParams?): List<User> {
        val sortParam = buildSortQuery(userQuery?.sortType, userQuery?.sortDirection)
        return executeApiSafely<PagedResponse<UserResponse>> {
            adminPanelApiService
                .getUsers(
                    query = userQuery?.searchInput,
                    sort = sortParam,
                    page = userQuery?.page,
                    size = userQuery?.size

                )
        }.toEntityList(UserResponse::toEntity)
    }

    override suspend fun updateUserStatus(userID: Uuid, status: Status) {
        executeApiSafely<Unit> {
            adminPanelApiService.updateUserStatus(userID.toString(), status.toString())
        }
    }
}