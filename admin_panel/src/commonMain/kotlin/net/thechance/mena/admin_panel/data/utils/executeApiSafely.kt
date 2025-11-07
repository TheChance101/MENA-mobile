package net.thechance.mena.admin_panel.data.utils

import de.jensklingenberg.ktorfit.Response
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.thechance.mena.admin_panel.data.remote.dto.ErrorDto
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException

internal suspend inline fun <reified T> executeApiSafely(
    noinline block: suspend () -> Response<T>
): T {
    val response = executeRequest(block)
    return handleResponse(response)
}

private suspend fun <T> executeRequest(block: suspend () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (e: IOException) {
        throw NoInternetException(e.message.orEmpty())
    } catch (e: UnresolvedAddressException){
        throw NoInternetException(e.message.orEmpty())
    } catch (e: Exception) {
        throw UnknownNetworkException(e.message.orEmpty())
    }
}

private inline fun <reified T> handleResponse(response: Response<T>): T {
    if (response.isSuccessful) {
        return parseBody<T>(response)
    }

    throw when {
        response.status == HttpStatusCode.Unauthorized ->
            UnauthorizedException("Unauthorized: " + parseErrorMessage(response))

        response.status == HttpStatusCode.RequestTimeout ->
            UnknownNetworkException("Request timeout: " + parseErrorMessage(response))

        response.status == HttpStatusCode.TooManyRequests ->
            UnknownNetworkException("Too many requests: " + parseErrorMessage(response))

        response.status.isServerError() ->
            UnknownNetworkException("Server error: " + parseErrorMessage(response))

        else ->
            UnknownNetworkException(parseErrorMessage(response))
    }
}

private inline fun <reified T> parseBody(response: Response<T>): T {
    return try {
        response.body()
            ?: throw UnknownNetworkException("Empty response body")
    } catch (_: SerializationException) {
        throw UnknownNetworkException("Error parsing response")
    } catch (_: Exception) {
        throw UnknownNetworkException("Unexpected error parsing response")
    }
}

private fun <T> parseErrorMessage(response: Response<T>): String {
    return try {
        (response.errorBody() as? String)?.let { errorBody ->
            Json.decodeFromString<ErrorDto>(errorBody).message
        } ?: "Unexpected error"
    } catch (_: Exception) {
        "Unexpected error"
    }
}

private fun HttpStatusCode.isServerError(): Boolean = this.value in 500..599