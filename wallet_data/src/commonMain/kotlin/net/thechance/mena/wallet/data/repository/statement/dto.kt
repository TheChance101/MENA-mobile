package net.thechance.mena.wallet.data.repository.statement

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
@OptIn(ExperimentalTime::class)
data class CachedTransactionsPdfDto(
    val pdf: ByteArray,
    val key: String,
    val timestamp: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CachedTransactionsPdfDto

        if (!pdf.contentEquals(other.pdf)) return false
        if (key != other.key) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pdf.contentHashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}