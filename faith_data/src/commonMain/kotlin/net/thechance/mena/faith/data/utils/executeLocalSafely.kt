package net.thechance.mena.faith.data.utils

import net.thechance.mena.faith.domain.exception.FaithException

suspend fun <T> executeLocalSafely(block: suspend () -> T): T {
    return try {
        block()
    } catch (_: Exception) {
        throw FaithException.UnknownException
    }
}