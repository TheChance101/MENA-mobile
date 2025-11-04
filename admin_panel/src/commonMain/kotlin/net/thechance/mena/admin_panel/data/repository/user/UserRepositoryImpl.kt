package net.thechance.mena.admin_panel.data.repository.user

import net.thechance.mena.admin_panel.data.mapper.toEntityPagedResult
import net.thechance.mena.admin_panel.data.mapper.user.buildSortQueries
import net.thechance.mena.admin_panel.data.mapper.user.toEntity
import net.thechance.mena.admin_panel.data.remote.api_service.UserApiService
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UpdateUserStatusRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.PagedResult
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
    override suspend fun getUsers(userQueryParams: UserQueryParams?): PagedResult<User> {
        val sortParam = buildSortQueries(
            property = userQueryParams?.sortType,
            direction = userQueryParams?.sortDirection
        )
        return executeApiSafely<PagedResponse<UserResponse>> {
            userApiService.getUsers(
                query = userQueryParams?.searchInput,
                sort = sortParam,
                page = userQueryParams?.page,
                size = userQueryParams?.size
            )
        }.toEntityPagedResult(UserResponse::toEntity)
    }

    override suspend fun updateUserStatus(userID: Uuid, status: User.Status) {
        executeApiSafely<Unit> {
            userApiService.updateUserStatus(
                userID.toString(),
                UpdateUserStatusRequestDto(status.toString())
            )
        }
    }
}