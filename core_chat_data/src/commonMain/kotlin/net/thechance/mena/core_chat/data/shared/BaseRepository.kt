package net.thechance.mena.core_chat.data.shared

import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException
import net.thechance.mena.core_chat.domain.exception.DataStoreException
import net.thechance.mena.core_chat.domain.exception.UnAuthorizedException
import net.thechance.mena.core_chat.domain.exception.UnknownException
import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException as ContactsProviderPermissionDeniedException

interface BaseRepository {

    suspend fun <T> tryNetworkCall(
        defaultException: (Throwable) -> ChatException = { e ->
            UnknownException("Unknown error occurred", e)
        },
        maxAttempts: Int = 1,
        call: suspend () -> BaseResponseDto<T>,
    ): T? {
        return runCatchingWithException(defaultException) {
            //TODO: handle network connection
            retry(maxAttempts) {
                val response = call()
                response.getSuccessBodyOrThrow()
            }
        }
    }

    suspend fun <T> retry(
        maxAttempts: Int = 3, block: suspend () -> T?
    ): T? {
        return try {
            block()
        } catch (e: ChatException) {
            throw e
        } catch (e: Throwable) {
            if (maxAttempts <= 1) throw e
            retry(
                maxAttempts = maxAttempts - 1, block = block
            )
        }
    }

    private suspend fun <T> runCatchingWithException(
        defaultException: (Throwable) -> ChatException, block: suspend () -> T?
    ): T? {
        return try {
            block()
        } catch (e: ContactsProviderPermissionDeniedException) {
            throw ContactsPermissionDeniedException("Contacts Permission Denied!", e)
        } catch (e: ChatException) {
            throw e
        } catch (e: Throwable) {
            throw defaultException(e)
        }
    }

    private fun <T> BaseResponseDto<T>.getSuccessBodyOrThrow(): T? {
        return when {
            this.success -> this.body
            this.status == STATUS_UNAUTHORIZED -> throw UnAuthorizedException(this.message)
            //TODO: handle more cases like 500, 404
            else -> throw UnknownException(this.message)
        }
    }


    suspend fun <T> tryCall(
        defaultException: (Throwable) -> ChatException,
        block: suspend () -> T
    ): T {
        return try {
            block()
        } catch (e: Exception) {
            throw defaultException(e)
        }
    }


    companion object {
        private const val STATUS_UNAUTHORIZED = 401
    }
}