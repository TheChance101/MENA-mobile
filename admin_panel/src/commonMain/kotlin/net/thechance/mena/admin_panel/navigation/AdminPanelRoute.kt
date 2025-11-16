package net.thechance.mena.admin_panel.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminPanelRoute

@Serializable
data object Splash : AdminPanelRoute

@Serializable
data object Login : AdminPanelRoute

@Serializable
data object UsersManagement : AdminPanelRoute

@Serializable
data object Deposit : AdminPanelRoute

@Serializable
data object DukanRequests : AdminPanelRoute

@Serializable
data object DukanManagement : AdminPanelRoute

@Serializable
data object DukanDetails : AdminPanelRoute