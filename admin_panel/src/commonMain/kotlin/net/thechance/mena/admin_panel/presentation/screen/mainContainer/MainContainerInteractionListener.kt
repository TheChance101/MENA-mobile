package net.thechance.mena.admin_panel.presentation.screen.mainContainer

interface MainContainerInteractionListener {
    fun onTabSelected(tab: MainContainerScreenState.SelectedSidebarTab)
    fun onLogOutClicked()
    fun onLogoutDismissed()
    fun onLogoutConfirmed()
}