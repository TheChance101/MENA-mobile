package net.thechance.mena.admin_panel.domain.model

data class UserQueryParams(
    val searchInput: String?,
    val sortType: SortType?,
    val sortDirection: SortDirection?,
    val page: Int ,
    val size: Int
)

enum class SortDirection(val value: String) {
    ASC("asc"),
    DESC("desc")
}
enum class SortType(val value: String) {
    USERNAME("username"),
    PHONE_NUMBER("phoneNumber"),
    LAST_LOGIN_DATE("lastLoginDate"),
    LAST_VISIT_DATE("lastVisitDate"),
    STATUS("status")
}