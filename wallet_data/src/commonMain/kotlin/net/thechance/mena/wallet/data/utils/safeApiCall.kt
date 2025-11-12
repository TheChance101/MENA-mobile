package net.thechance.mena.wallet.data.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.wallet.data.dto.ErrorDto
import net.thechance.mena.wallet.domain.exceptions.BlockedReceiverException
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException

suspend inline fun <reified T> safeApiCall(
    noinline execute: suspend () -> HttpResponse
): T {
    val response = executeRequest(execute)
    return handleResponse(response)
}

suspend fun executeRequest(execute: suspend () -> HttpResponse): HttpResponse {
    return try {
        execute()
    } catch (e: IOException) {
        throw NoInternetException(e.message.orEmpty())
    } catch (e: Exception) {
        throw UnknownNetworkException(e.message.orEmpty())
    }
}

suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
    if (response.status.value == HttpStatusCode.NoContent.value)
        throw NoDataFoundException("No Data Found: " + parseErrorMessage(response))

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

        HttpStatusCode.Forbidden ->
            throw BlockedReceiverException("Receiver is blocked: " + parseErrorMessage(response))

        in getServerErrorRange() ->
            throw UnknownNetworkException("Server error: " + parseErrorMessage(response))

        else ->
            throw UnknownNetworkException(parseErrorMessage(response))
    }
}

suspend inline fun <reified T> parseBody(response: HttpResponse): T {
    if (response.headers[HttpHeaders.ContentType] == ContentType.Application.Pdf.toString()) {
        return response as T
    }
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