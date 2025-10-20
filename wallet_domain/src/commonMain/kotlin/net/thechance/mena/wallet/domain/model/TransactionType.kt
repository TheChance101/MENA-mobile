package net.thechance.mena.wallet.domain.model

enum class TransactionType {
    SENT,
    RECEIVED,
    ONLINE_PURCHASE;

    companion object {
        fun valueOfOrDefault(value: String?): TransactionType {
            return runCatching { value?.let { TransactionType.valueOf(it) } ?: SENT }.getOrDefault (SENT)
        }
    }
}