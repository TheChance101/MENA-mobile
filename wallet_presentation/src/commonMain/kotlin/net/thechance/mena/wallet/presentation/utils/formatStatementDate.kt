package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDate

expect fun formatStatementDate(date: LocalDate, outputFormat: String = "MMM dd yyyy"): String