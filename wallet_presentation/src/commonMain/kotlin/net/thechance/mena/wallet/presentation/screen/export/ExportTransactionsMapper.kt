package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType

fun FilterType.toDomain(): TransactionType {
    return when (this) {
        FilterType.SENT -> TransactionType.SENT
        FilterType.RECEIVED -> TransactionType.RECEIVED
        FilterType.ONLINE_PURCHASE -> TransactionType.ONLINE_PURCHASE
    }
}

fun FilterStatus.toDomain(): TransactionStatus? {
    return when (this) {
        FilterStatus.ALL -> null
        FilterStatus.FAILED -> TransactionStatus.FAILED
        FilterStatus.SUCCESS -> TransactionStatus.SUCCESS
    }
}