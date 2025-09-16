package net.thechance.mena.trends.data.util

suspend inline fun <T> safeApiCall(
    execute: suspend () -> T
): T {
    val result = try {
        execute()
    } catch (e: Exception) {
        throw e // TODO: handle exceptions after adding domain exceptions
    }

    return handleErrorStatus(result)
}

fun <T> handleErrorStatus(result: T): T {
    return result // TODO: map error status numbers to exceptions after adding domain exceptions
}