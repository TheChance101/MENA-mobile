package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType

fun buildSortQuery(property: SortType?, direction: SortDirection?): String {
    val directionStr = convertDirectionToString(direction)
    val propertyStr=convertTypeToString(property)
    return "$propertyStr,$directionStr"
}
private fun convertDirectionToString(direction: SortDirection?) :String{
    val directionStr = when (direction) {
        SortDirection.ASC -> "asc"
        SortDirection.DESC -> "desc"
        null -> "asc"
    }
    return directionStr
}
private fun convertTypeToString(type: SortType?):String {
    val typeStr = when (type) {
        SortType.PHONE_NUMBER-> "phoneNumber"
        SortType.USERNAME -> "username"
        SortType.LAST_LOGIN_DATE ->"lastLoginDate"
        SortType.LAST_VISIT_DATE -> "lastVisitDate"
        else->"username"
    }
    return typeStr
}
