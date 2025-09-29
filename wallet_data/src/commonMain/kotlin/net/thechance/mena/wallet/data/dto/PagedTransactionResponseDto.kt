package net.thechance.mena.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedTransactionResponseDto (
    @SerialName("totalElements")
    val totalElements : Long? ,
    @SerialName("page")
    val page : Long?,
    @SerialName("pageSize")
    val pageSize : Long?,
    @SerialName("totalPages")
    val totalPages : Long?,
    @SerialName("transactions")
    val transactions : List<TransactionDto>?
)