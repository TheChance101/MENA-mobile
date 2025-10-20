package net.thechance.mena.wallet.data.dto.remote

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirstTransactionDateDto(
    @SerialName("firstTransactionDate")
    val firstTransactionDate: LocalDate? = null
)
