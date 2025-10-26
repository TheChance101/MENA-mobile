package net.thechance.mena.admin_panel.data.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.admin_panel.data.dto.ErrorDto
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException

internal suspend inline fun <reified T> safeApiCall(
    noinline execute: suspend () -> HttpResponse
): T {
    val response = executeRequest(execute)
    return handleResponse(response)
}

private suspend fun executeRequest(execute: suspend () -> HttpResponse): HttpResponse {
    return try {
        execute()
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

    when (response.status) {
        HttpStatusCode.Unauthorized ->
            throw UnknownNetworkException("Unauthorized: " + parseErrorMessage(response))

        HttpStatusCode.RequestTimeout ->
            throw UnknownNetworkException("Request timeout: " + parseErrorMessage(response))

        HttpStatusCode.TooManyRequests ->
            throw UnknownNetworkException("Too many requests: " + parseErrorMessage(response))

        in getServerErrorRange() ->
            throw UnknownNetworkException("Server error: " + parseErrorMessage(response))

        else ->
            throw UnknownNetworkException(parseErrorMessage(response))
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

fun getServerErrorRange() =
    HttpStatusCode.InternalServerError..HttpStatusCode.InsufficientStorage