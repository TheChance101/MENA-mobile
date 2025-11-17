package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.ITEMS_COUNT
import net.thechance.mena.admin_panel.presentation.utils.format
import net.thechance.mena.admin_panel.presentation.utils.formatPhoneNumber
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toUIState(currentPage: Int, indexInList: Int): UsersManagementScreenState.UserItem {
    val startIndex = currentPage * ITEMS_COUNT
    return UsersManagementScreenState.UserItem(
        index = startIndex + indexInList + 1,
        id = id,
        fullName = "$firstName $lastName",
        phoneNumber = formatPhoneNumber(phoneNumber),
        lastLoginAt = lastLoginAt.format("dd-MM-yyyy"),
        lastVisitAt = lastVisitAt.format("dd-MM-yyyy"),
        status = status
    )
}

fun UsersManagementScreenState.SortType.toEntity(): SortType? = when (this) {
    UsersManagementScreenState.SortType.USERNAME -> SortType.USERNAME
    UsersManagementScreenState.SortType.LAST_LOGIN_DATE -> SortType.LAST_LOGIN_DATE
    UsersManagementScreenState.SortType.LAST_VISIT_DATE -> SortType.LAST_VISIT_DATE
    UsersManagementScreenState.SortType.ACTIVATION_STATUS -> SortType.ACTIVATION_STATUS
}

fun UsersManagementScreenState.SortDirection.toEntity(): SortDirection? = when (this) {
    UsersManagementScreenState.SortDirection.ASC -> SortDirection.ASC
    UsersManagementScreenState.SortDirection.DESC -> SortDirection.DESC
}