package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType

fun buildSortQuery(property: SortType?, direction: SortDirection?): String {
    val directionStr = convertDirectionToString(direction)
    val propertyStr=convertTypeToString(property)
    return "$propertyStr,$directionStr"
}
private fun convertDirectionToString(direction: SortDirection?) :String{
    return when (direction) {
        SortDirection.ASC -> "asc"
        SortDirection.DESC -> "desc"
        null -> "asc"
    }
}
private fun convertTypeToString(type: SortType?):String {
   return  when (type) {
        SortType.USERNAME -> "firstName"
        SortType.LAST_LOGIN_DATE ->"lastLoginAt"
        SortType.LAST_VISIT_DATE -> "lastVisitAt"
        else->"firstName"
    }
}
