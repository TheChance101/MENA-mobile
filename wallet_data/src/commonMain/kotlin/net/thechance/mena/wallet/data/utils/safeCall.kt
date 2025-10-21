package net.thechance.mena.wallet.data.utils

import net.thechance.mena.wallet.domain.exceptions.WalletException

suspend fun <T> safeCall(
    defaultException: () -> WalletException,
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (_: Exception) {
        throw defaultException()
    }
}