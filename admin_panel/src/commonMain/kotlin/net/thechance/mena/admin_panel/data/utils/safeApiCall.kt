package net.thechance.mena.admin_panel.data.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.admin_panel.data.remote.dto.ErrorDto
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException

internal suspend inline fun <reified T> wrapApiCall(
    noinline block: suspend () -> HttpResponse
): T {
    val response = executeRequest(block)
    return handleResponse(response)
}

private suspend fun executeRequest(block: suspend () -> HttpResponse): HttpResponse {
    return try {
        block()
    } catch (e: IOException) {
        throw NoInternetException(e.message.orEmpty())
    } catch (e: Exception) {
        throw UnknownNetworkException(e.message.orEmpty())
    }
}

private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
    if (response.status.isSuccess()) {
        return parseBody<T>(response)
    }

    throw when {
        response.status == HttpStatusCode.Unauthorized ->
            UnknownNetworkException("Unauthorized: " + parseErrorMessage(response))

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

suspend inline fun <reified T> parseBody(response: HttpResponse): T {
    return try {
        response.body()
    } catch (_: SerializationException) {
        throw UnknownNetworkException("Error parsing response")
    } catch (_: Exception) {
        throw UnknownNetworkException("Unexpected error parsing response")
    }
}

suspend fun parseErrorMessage(response: HttpResponse): String {
    return try {
        response.body<ErrorDto>().message ?: "Unexpected error"
    } catch (_: Exception) {
        "Unexpected error"
    }
}

private fun HttpStatusCode.isServerError(): Boolean = this.value in 500..599