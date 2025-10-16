package net.thechance.mena.core_chat.domain.exception

abstract class ChatException(logMessage: String, cause: Throwable? = null) :
    Exception(logMessage, cause)

class ContactSyncFailedException(logMessage: String, cause: Throwable? = null) :
    ChatException(logMessage, cause)

class ContactsFetchFailedException(logMessage: String, cause: Throwable? = null) :
    ChatException(logMessage, cause)

class ContactsPermissionDeniedException(logMessage: String, cause: Throwable? = null) :
        ChatException(logMessage, cause)

class UnAuthorizedException(logMessage: String? = null) :
    ChatException(logMessage ?: "User is not authorized")

class UnknownException(logMessage: String? = null, cause: Throwable? = null) :
    ChatException(logMessage ?: "Unknown error", cause)

class DataStoreException(logMessage: String, cause: Throwable? = null) :
    ChatException(logMessage, cause)

class SendMessageFailedException(logMessage: String, cause: Throwable? = null) :
        ChatException(logMessage, cause)

class NotFoundException(logMessage: String, cause: Throwable? = null) : ChatException(logMessage, cause)

class OperationFailedException(logMessage: String, cause: Throwable? = null) : ChatException(logMessage, cause)