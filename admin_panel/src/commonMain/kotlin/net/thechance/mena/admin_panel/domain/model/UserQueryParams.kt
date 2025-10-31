package net.thechance.mena.admin_panel.domain.model

data class UserQueryParams(
    val searchInput: String?,
    val sortType: String?,
    val sortDirection: SortDirection?,
    val page: Int = 0,
    val size: Int = 20
)

enum class SortDirection(val value: String) {
    ASC("asc"),
    DESC("desc")
}