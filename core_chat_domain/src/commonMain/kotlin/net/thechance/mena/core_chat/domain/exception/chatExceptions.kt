package net.thechance.mena.core_chat.domain.exception

abstract class ChatException(logMessage: String) :
    Exception(logMessage)

class ContactSyncFailedException(logMessage: String) :
    ChatException(logMessage)

class ContactsFetchFailedException(logMessage: String) :
    ChatException(logMessage)

class ContactsPermissionDeniedException(logMessage: String) :
        ChatException(logMessage)

class UnAuthorizedException(logMessage: String? = null) :
    ChatException(logMessage ?: "User is not authorized")

class UnknownException(logMessage: String? = null) :
    ChatException(logMessage ?: "Unknown error")

class DataStoreException(logMessage: String) :
    ChatException(logMessage)

class SendMessageFailedException(logMessage: String) :
        ChatException(logMessage)

class NotFoundException(logMessage: String) : ChatException(logMessage)

class OperationFailedException(logMessage: String) : ChatException(logMessage)