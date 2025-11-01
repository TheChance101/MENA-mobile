package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType

fun buildSortQuery(property: SortType?, direction: SortDirection?): String? {
    if (property == null) return null
    val directionStr = direction?.value ?: SortDirection.ASC.value
    val propertyStr=property?.value?: SortType.PHONE_NUMBER.value
    return "$propertyStr,$directionStr"
}