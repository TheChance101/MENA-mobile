package net.thechance.mena.admin_panel.domain.model

data class UserQueryParams(
    val searchInput: String?,
    val sortType: String?,
    val sortDirection: SortDirection?,
    val page: Int ,
    val size: Int
)

enum class SortDirection(val value: String) {
    ASC("asc"),
    DESC("desc")
}