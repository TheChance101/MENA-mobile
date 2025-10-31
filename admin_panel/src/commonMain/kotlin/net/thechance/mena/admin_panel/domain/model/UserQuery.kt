package net.thechance.mena.admin_panel.domain.model

data class UserQuery(
    val searchInput: String?,
    val sortType: String?,
    val sortDirection: SortDirection?,
    val page: Int = 0,
    val size: Int = 20

)
enum class SortDirection {
    ASC,
    DESC
}
