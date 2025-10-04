package net.thechance.mena.wallet.presentation.navigation

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

fun TransactionFilterParams.toRoute(): ViewTransactionsStatementScreenRoute {
    return ViewTransactionsStatementScreenRoute(
        types = types,
        status = status,
        startDate = startDate.toString(),
        endDate = endDate.toString()
    )
}

fun ViewTransactionsStatementScreenRoute.toFilterParams(): TransactionFilterParams {
    return TransactionFilterParams(
        types = types,
        status = status,
        startDate = startDate?.let(LocalDate::parse),
        endDate = endDate?.let(LocalDate::parse)
    )
}