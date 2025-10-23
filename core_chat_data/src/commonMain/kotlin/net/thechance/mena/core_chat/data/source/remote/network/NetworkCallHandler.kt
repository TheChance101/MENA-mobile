package net.thechance.mena.core_chat.data.source.remote.network

import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import net.thechance.mena.core_chat.domain.exception.*

suspend fun <T> tryNetworkCall(
    defaultException: ChatException = UnknownException("Unknown error occurred"),
    bodyType: TypeInfo,
    call: suspend () -> HttpResponse,
): T? {
    return runCatchingWithException(defaultException) {
        val response = call()
        response.getSuccessBodyOrThrow(bodyType)
    }
}

private suspend fun <T> runCatchingWithException(
    defaultException: ChatException = UnknownException("Unknown error occurred"),
    block: suspend () -> T?
): T? {
    return try {
        block()
    } catch (e: ContactsPermissionDeniedException) {
        throw ContactsPermissionDeniedException("Contacts Permission Denied!")
    } catch (e: ChatException) {
        throw e
    } catch (e: Throwable) {
        throw defaultException
    }
}


private suspend fun <T> HttpResponse.getSuccessBodyOrThrow(bodyType: TypeInfo): T? {
    return when {
        this.status.isSuccess() -> this.body(bodyType)
        this.status == HttpStatusCode.Unauthorized -> throw UnAuthorizedException()
        this.status == HttpStatusCode.NotFound -> throw NotFoundException(this.status.description)
        else -> throw UnknownException(this.status.description)
    }
}
