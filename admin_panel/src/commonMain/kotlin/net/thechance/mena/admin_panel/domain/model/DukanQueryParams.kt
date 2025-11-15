package net.thechance.mena.admin_panel.domain.model

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan

data class DukanQueryParams(
    val searchInput: String?,
    val status: Dukan.Status,
    val activationStatus: Dukan.ActivationStatus?,
    val sortType: DukansSortType?,
    val sortDirection: SortDirection?,
    val page: Int,
    val size: Int
)

enum class DukansSortType() {
    NAME,
    CREATED_AT,
    ACTIVATION_STATUS,
}