package net.thechance.mena.wallet.domain.model

enum class TransactionStatus {
    SUCCESS,
    FAILED;

    companion object {
        fun valueOfOrDefault(value: String?): TransactionStatus {
            return runCatching { value?.let { valueOf(it) } ?: SUCCESS }.getOrDefault(SUCCESS)
        }
    }
}