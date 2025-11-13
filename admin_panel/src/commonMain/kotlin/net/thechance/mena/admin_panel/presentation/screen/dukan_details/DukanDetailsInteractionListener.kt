package net.thechance.mena.admin_panel.presentation.screen.dukan_details

interface DukanDetailsInteractionListener {
    fun onBackBtnClicked()
    fun onChangeDukanStatusBtnClicked()
    fun onNextShelvesPageRequested()
    fun onShelfSelected(shelfId: String)
    fun onNextProductsPageRequested()
    fun onDeactivateDukanDialogDismissed()
    fun onConfirmDukanDeactivationBtnClicked()
    fun onDeactivateReasonChanged(reason: String)
}