package net.thechance.mena.trends.data.util

import co.touchlab.kermit.Logger
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import net.thechance.mena.trends.domain.exception.NoInternetException

internal suspend inline fun <reified T> safeApiCall(
    execute: suspend () -> HttpResponse
): T {
    val result = try {
        execute()
    } catch (exception: IOException) {
        logError(SAFE_API_CALL_TAG, "IOException", exception.message.toString())
        throw NoInternetException()
    } catch (exception: UnresolvedAddressException) {
        logError(SAFE_API_CALL_TAG, "UnresolvedAddressException", exception.message.toString())
        throw NoInternetException()
    } catch (exception: Exception) {
        logError(SAFE_API_CALL_TAG, "Unknown exception", exception.message.toString())
        throw exception
    }

    return handleResponseStatusCode(result)
}

private suspend inline fun <reified T> handleResponseStatusCode(result: HttpResponse): T {
    return when (result.status.value) {
        in 200..299 -> {
            result.body<T>()
        }

        in 400..499 -> {
            when (result.status) {
                HttpStatusCode.Unauthorized -> {
                    logError(
                        HANDLE_ERROR_STATUS_TAG,
                        "Unauthorized",
                        "Not authorized to do this action"
                    )
                    throw Exception()
                }

                HttpStatusCode.NotFound -> {
                    logError(
                        HANDLE_ERROR_STATUS_TAG,
                        "Not found",
                        "the resource you requested could not be found"
                    )
                    throw Exception()
                }

                else -> {
                    logError(
                        HANDLE_ERROR_STATUS_TAG,
                        "Unknown 400s status code ${result.status.value}",
                        "An error with status code ${result.status.value} happened"
                    )
                    throw Exception()
                }
            }
        }

        in 500..599 -> {
            logError(
                HANDLE_ERROR_STATUS_TAG,
                "Server error",
                "An error occurred on the server side"
            )
            throw Exception()
        }

        else -> {
            logError(
                HANDLE_ERROR_STATUS_TAG,
                "Unknown status code ${result.status.value}",
                "An error with status code ${result.status.value} happened"
            )
            throw Exception()
        }
    }
}

private fun logError(
    tag: String,
    type: String,
    message: String
) {
    Logger.e(tag) { "$type: $message" }
}

private const val SAFE_API_CALL_TAG = "safeApiCall"
private const val HANDLE_ERROR_STATUS_TAG = "handleErrorStatus"
