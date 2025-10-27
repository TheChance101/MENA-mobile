package net.thechance.mena.admin_panel.domain.exceptions

open class AdminPanelException(message: String = "Admin Panel Exception") : Exception(message)

class NoInternetException(message: String = "No Internet") : AdminPanelException(message)

class UnknownNetworkException(message: String = "Unknown") : AdminPanelException(message)

