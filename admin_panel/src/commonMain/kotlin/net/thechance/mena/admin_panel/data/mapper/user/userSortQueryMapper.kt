package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType

fun buildSortQueries(property: SortType?, direction: SortDirection?): List<String> {
    val directionStr = convertDirectionToString(direction)
    return when (property) {
        SortType.USERNAME -> listOf("firstName,$directionStr", "lastName,$directionStr")
        SortType.LAST_LOGIN_DATE -> listOf("lastLoginAt,$directionStr")
        SortType.LAST_VISIT_DATE -> listOf("lastVisitAt,$directionStr")
        SortType.ACTIVATION_STATUS -> listOf("status,$directionStr")
        else -> listOf("firstName,$directionStr", "lastName,$directionStr")
    }
}

private fun convertDirectionToString(direction: SortDirection?): String {
    return when (direction) {
        SortDirection.ASC -> "asc"
        SortDirection.DESC -> "desc"
        null -> "asc"
    }
}