package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

interface DukanRequestsInteractionListener {
    fun onSortClicked(type: DukanRequestsScreenState.SortType)
    fun onViewDetailsClicked(dukanId: Uuid)
    fun onRetryClicked()
    fun onPageChanged(page: Int)
    fun onApproveDukanClicked()
    fun onRejectDukanClicked()
    fun onRejectDukanDialogDismissed()
    fun onRejectDukanConfirmed()
    fun onRejectionMessageChanged(reason: String)
    fun onDismissDukanDetails()
}