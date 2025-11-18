package net.thechance.mena.admin_panel.domain.model

data class UserQueryParams(
    val searchInput: String?,
    val sortType: SortType?,
    val sortDirection: SortDirection?,
    val page: Int,
    val size: Int
)

enum class SortType() {
    USERNAME,
    LAST_LOGIN_DATE,
    LAST_VISIT_DATE,
    ACTIVATION_STATUS
}