package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.domain.model.SortDirection

fun buildSortQuery(property: String?, direction: SortDirection?): String? {
    if (property == null) return null

    val directionStr = direction?.value ?: SortDirection.ASC.value

    return "$property,$directionStr"
}