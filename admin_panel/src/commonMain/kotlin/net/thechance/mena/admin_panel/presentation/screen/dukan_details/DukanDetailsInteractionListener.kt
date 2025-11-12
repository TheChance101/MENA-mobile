package net.thechance.mena.admin_panel.presentation.screen.dukan_details

interface DukanDetailsInteractionListener {
    fun onBackBtnClicked()
    fun onChangeDukanStatusBtnClicked()
    fun onNextShelvesPageRequested()
    fun onShelfSelected(shelfId: String)
}