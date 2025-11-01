package net.thechance.mena.admin_panel.data.repository.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.thechance.mena.admin_panel.data.mapper.toEntityList
import net.thechance.mena.admin_panel.data.mapper.user.buildSortQuery
import net.thechance.mena.admin_panel.data.mapper.user.toEntity
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UpdateUserStatusRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.data.remote.service.UserApiService
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
    private val userApiService: UserApiService,
) : UserRepository {
    override suspend fun getUsers(userQueryParams: UserQueryParams?): List<User> {
        val sortParam = buildSortQuery(userQueryParams?.sortType, userQueryParams?.sortDirection)
        return executeApiSafely<PagedResponse<UserResponse>> {
            userApiService.getUsers(
                query = userQueryParams?.searchInput,
                sort = sortParam,
                page = userQueryParams?.page,
                size = userQueryParams?.size
            )
        }.toEntityList(UserResponse::toEntity)
    }
    override suspend fun updateUserStatus(userID: Uuid, status: Status) {
        executeApiSafely<Unit> {
            userApiService.updateUserStatus(
                userID.toString(),
                UpdateUserStatusRequestDto(status.toString())
            )
        }
    }
}