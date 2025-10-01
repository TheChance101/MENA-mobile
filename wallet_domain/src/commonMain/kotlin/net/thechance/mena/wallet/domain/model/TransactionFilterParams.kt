package net.thechance.mena.wallet.domain.model

import kotlinx.datetime.LocalDate

data class TransactionFilterParams(
    val page: Int?,
    val pageSize: Int?,
    val types: List<TransactionType>? = null,
    val status: TransactionStatus? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
