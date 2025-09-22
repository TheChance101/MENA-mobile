package net.thechance.mena.dukan.domain.exceptions

open class DukanException(message: String = "Dukan Exception") : Exception(message)

class DukanNotFoundException(message: String = "Dukan Not Found") : DukanException(message)
class DukanNameTakenException(message: String = "Dukan Name Taken") : DukanException(message)
class NoInternetException(message: String = "No Internet") : DukanException(message)