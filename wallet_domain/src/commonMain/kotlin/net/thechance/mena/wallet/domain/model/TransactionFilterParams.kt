package net.thechance.mena.wallet.domain.model

import kotlinx.datetime.LocalDate

data class TransactionFilterParams(
    val page: Int=1,
    val pageSize: Int=20,
    val types: List<TransactionType>? = null,
    val status: TransactionStatus? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
