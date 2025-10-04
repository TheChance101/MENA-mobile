package net.thechance.mena.wallet.data.repository.statement

import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun TransactionFilterParams?.key(): String {
    if (this == null) return "ALL_ANY_NONE_NONE"

    val typesKey = types?.takeIf { it.isNotEmpty() }?.joinToString(",") ?: "ALL"
    val statusKey = status?.name ?: "ANY"
    val startKey = startDate?.toString() ?: "NONE"
    val endKey = endDate?.toString() ?: "NONE"
    return "${typesKey}_${statusKey}_${startKey}_${endKey}"
}

@OptIn(ExperimentalTime::class)
fun ByteArray.toCachedTransactionsPdfDto(key: String): CachedTransactionsPdfDto {
    return CachedTransactionsPdfDto(
        pdf = this,
        key = key,
        timestamp = Clock.System.now()
    )
}