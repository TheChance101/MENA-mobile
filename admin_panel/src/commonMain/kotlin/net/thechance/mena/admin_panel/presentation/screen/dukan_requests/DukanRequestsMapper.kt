package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.presentation.utils.format
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Dukan.toUIState(currentPage: Int, indexInList: Int): DukanRequestsScreenState.DukanItem {
    val startIndex = currentPage * ITEMS_COUNT
    return DukanRequestsScreenState.DukanItem(
        index = startIndex + indexInList + 1,
        id = id,
        name = name,
        imageUrl = imageUrl,
        address = address,
        coordinates = DukanRequestsScreenState.DukanItem.CoordinatesUiState(
            latitude = latitude,
            longitude = longitude
        ),
        date = createdAt.format("dd-MM-yyyy"),
        categories = categories.map { it.title }
    )
}

fun DukanRequestsScreenState.SortType.toEntity(): DukansSortType? = when (this) {
    DukanRequestsScreenState.SortType.DUKAN_NAME -> DukansSortType.NAME
    DukanRequestsScreenState.SortType.DATE -> DukansSortType.CREATED_AT
}

fun DukanRequestsScreenState.SortDirection.toEntity(): SortDirection? = when (this) {
    DukanRequestsScreenState.SortDirection.ASC -> SortDirection.ASC
    DukanRequestsScreenState.SortDirection.DESC -> SortDirection.DESC
}

const val ITEMS_COUNT = 8