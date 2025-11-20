package net.thechance.mena.identity.data.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.exception.UnknownException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException

suspend fun <T> safeWrapper(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: ClientRequestException) {
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> throw UnAuthorizedException()
            HttpStatusCode.NotFound -> throw InvalidCredentialsException()
            HttpStatusCode.Forbidden -> throw UserIsBlockedException()
            HttpStatusCode.TooManyRequests -> throw TooManyRequestsException()
            HttpStatusCode.BadRequest -> throw InvalidRequestException()
            HttpStatusCode.Conflict -> throw PhoneNumberAlreadyExistsException()
            else -> throw UnknownException()
        }
    } catch (e: Exception) {
        when(e){
            is UnresolvedAddressException -> throw NoNetworkException()
            is HttpRequestTimeoutException -> throw NoNetworkException()
            else -> throw e
        }
    }
}
