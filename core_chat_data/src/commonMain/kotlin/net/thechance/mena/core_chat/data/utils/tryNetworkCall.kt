package net.thechance.mena.core_chat.data.utils

import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.UnAuthorizedException
import net.thechance.mena.core_chat.domain.exception.UnknownException

suspend fun <T> tryNetworkCall(
    defaultException: (Throwable) -> ChatException = { e ->
        UnknownException("Unknown error occurred")
    },
    bodyType: TypeInfo,
    maxAttempts: Int = 1,
    call: suspend () -> HttpResponse,
): T? {
    return runCatchingWithException(defaultException) {
        retry(maxAttempts) {
            val response = call()
            response.getSuccessBodyOrThrow(bodyType)
        }
    }
}

private suspend fun <T> retry(
    maxAttempts: Int,
    block: suspend () -> T?
): T? {
    return try {
        block()
    } catch (e: ChatException) {
        throw e
    } catch (e: Throwable) {
        if (maxAttempts <= 1) throw e
        retry(maxAttempts = maxAttempts - 1, block = block)
    }
}

private suspend fun <T> runCatchingWithException(
    defaultException: (Throwable) -> ChatException,
    block: suspend () -> T?
): T? {
    return try {
        block()
    } catch (_: ContactsPermissionDeniedException) {
        throw net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException(
            "Contacts Permission Denied!")
    } catch (e: ChatException) {
        throw e
    } catch (e: Throwable) {
        throw defaultException(e)
    }
}

private suspend fun <T> HttpResponse.getSuccessBodyOrThrow(bodyType: TypeInfo): T? {
    return when {
        this.status.isSuccess() -> this.body(bodyType)
        this.status == HttpStatusCode.Companion.Unauthorized -> throw UnAuthorizedException()
        this.status == HttpStatusCode.Companion.NotFound -> throw NotFoundException(this.status.description)
        else -> throw UnknownException(this.status.description)
    }
}
