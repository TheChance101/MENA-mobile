package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

interface DukanRequestsInteractionListener {
    fun onSortClicked(type: DukanRequestsScreenState.SortType)
    fun onViewDetailsClicked(selectedDukan: DukanRequestsScreenState.DukanItem)
    fun onRetryClicked()
    fun onPageChanged(page: Int)
    fun onApproveDukanClicked()
    fun onRejectDukanClicked()
    fun onRejectDukanDialogDismissed()
    fun onRejectDukanConfirmed()
    fun onRejectionMessageChanged(reason: String)
    fun onDukanDetailsDismissed()
}