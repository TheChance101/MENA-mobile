@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.presentation.utils.format
import kotlin.uuid.ExperimentalUuidApi

fun toggleSortDirection(currentSortDirection: SortDirection): SortDirection =
    if (currentSortDirection == SortDirection.ASC) SortDirection.DESC else SortDirection.ASC

fun Dukan.toUIState(
    currentPage: Int,
    indexInList: Int,
    itemCount: Int
): DukanManagementScreenState.Dukan {
    val startIndex = currentPage * itemCount
    return DukanManagementScreenState.Dukan(
        id = id,
        index = startIndex + indexInList + 1,
        name = name,
        location = address,
        addedDate = createdAt.format("dd-MM-yyyy"),
        isActive = activationStatus == Dukan.ActivationStatus.ACTIVATED,
        imageUrl = imageUrl
    )
}