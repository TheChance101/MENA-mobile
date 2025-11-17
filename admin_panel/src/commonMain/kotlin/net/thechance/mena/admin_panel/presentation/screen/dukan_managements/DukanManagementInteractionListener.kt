package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import net.thechance.mena.admin_panel.domain.model.DukansSortType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanManagementInteractionListener {
    fun onSearchQueryChanged(query: String)
    fun onClearQueryClicked()
    fun onRetryClicked()
    fun onViewDetailsClicked(dukanId: Uuid)
    fun onSortClicked(type: DukansSortType)
    fun onPageChanged(page: Int)
}