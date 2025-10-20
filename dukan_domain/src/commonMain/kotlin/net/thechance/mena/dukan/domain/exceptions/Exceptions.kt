package net.thechance.mena.dukan.domain.exceptions

open class DukanException(message: String = "Dukan Exception") : Exception(message)

class NoSuchItemException(message: String = "Item Not Found") : DukanException(message)

class DuplicateNameException(message: String = "Name Already Exists") : DukanException(message)
class CreationFailedException(message: String = "Creation Failed") : DukanException(message)
class UploadingFailedException(message: String = "Uploading Failed") : DukanException(message)
class InvalidImageFormatException(message: String = "Invalid Format") : DukanException(message)
class UnAuthorizedException(message: String = "UnAuthorized") : DukanException(message)

class DeletionNotAllowedException(message: String = "Deletion Not Allowed") : DukanException(message)
class NoInternetException(message: String = "No Internet") : DukanException(message)