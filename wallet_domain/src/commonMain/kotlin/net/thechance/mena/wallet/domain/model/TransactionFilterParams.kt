package net.thechance.mena.wallet.domain.model

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType

data class TransactionFilterParams(
    val types: List<TransactionType>? = null,
    val status: TransactionStatus? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
