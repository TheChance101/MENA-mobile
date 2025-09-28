package net.thechance.mena.core_chat.data.shared

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException
import net.thechance.mena.core_chat.domain.exception.UnAuthorizedException
import net.thechance.mena.core_chat.domain.exception.UnknownException
import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException as ContactsProviderPermissionDeniedException

interface BaseRepository {

    suspend fun <T> tryNetworkCall(
        defaultException: (Throwable) -> ChatException = { e ->
            UnknownException("Unknown error occurred", e)
        },
        bodyType: TypeInfo,
        maxAttempts: Int = 1,
        call: suspend () -> HttpResponse,
    ): T? {
        return runCatchingWithException(defaultException) {
            //TODO: handle network connection
            retry(maxAttempts) {
                val response = call()
                response.getSuccessBodyOrThrow(bodyType)
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

    private suspend fun <T> HttpResponse.getSuccessBodyOrThrow(bodyType: TypeInfo): T? {
        return when {
            this.status.isSuccess() -> this.body(bodyType)
            this.status == HttpStatusCode.Unauthorized -> throw UnAuthorizedException()
            //TODO: handle more cases like 500, 404
            else -> throw UnknownException(this.status.description)
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
}