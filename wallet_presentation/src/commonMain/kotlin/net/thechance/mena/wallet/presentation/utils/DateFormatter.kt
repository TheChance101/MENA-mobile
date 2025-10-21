package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime


expect fun formatLocalDateTime(date: LocalDateTime, outputFormat: String): String

expect fun formatLocalDate(date: LocalDate, outputFormat: String): String