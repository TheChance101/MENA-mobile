package net.thechance.mena.admin_panel.data.mapper.dukan

import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.domain.model.SortDirection

fun buildSortQueries(
    property: DukansSortType?,
    direction: SortDirection?
): String {
    val directionStr = convertDirectionToString(direction)
    return when (property) {
        DukansSortType.NAME -> "name,$directionStr"
        DukansSortType.CREATED_AT -> "createdAt,$directionStr"
        DukansSortType.ACTIVATION_STATUS -> "activationStatus,$directionStr"
        else -> "name,$directionStr"
    }
}

private fun convertDirectionToString(direction: SortDirection?): String {
    return when (direction) {
        SortDirection.ASC -> "asc"
        SortDirection.DESC -> "desc"
        null -> "asc"
    }
}