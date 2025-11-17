package net.thechance.mena.admin_panel.domain.exceptions

open class AdminPanelException(message: String = "Admin Panel Exception") : Exception(message)

class NoInternetException(message: String = "No Internet") : AdminPanelException(message)

class UnknownNetworkException(message: String = "Unknown error") : AdminPanelException(message)

class InvalidPasswordException(message: String = "password doesn't match validations") :
    AdminPanelException(message)

class InvalidPhoneNumberException(
    message: String = "Invalid phone number format for selected country"
) : AdminPanelException(message)

class InvalidAmountException(message: String = "Amount must be greater than zero") :
    AdminPanelException(message)

class UnauthorizedException(message: String = "Invalid user name or password") :
    AdminPanelException(message)