package net.thechance.mena.wallet.data.mapper

import io.ktor.http.Parameters
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

fun TransactionFilterParams.toParameters(): Parameters =
    Parameters.build {
        types?.forEach {
            append("type", it.name)
        }
        status?.let { append("status", it.name) }
        startDate?.let { append("startDate", it.toString()) }
        endDate?.let { append("endDate", it.toString()) }
    }