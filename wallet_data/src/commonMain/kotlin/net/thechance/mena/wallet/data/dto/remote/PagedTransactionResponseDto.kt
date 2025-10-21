package net.thechance.mena.wallet.data.dto.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedTransactionResponseDto(
    @SerialName("totalElements")
    val totalElements: Long? = null,
    @SerialName("page")
    val page: Long? = null,
    @SerialName("pageSize")
    val pageSize: Long? = null,
    @SerialName("totalPages")
    val totalPages: Long? = null,
    @SerialName("transactions")
    val transactions: List<TransactionDto>? = null
)