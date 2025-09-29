package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDateTime


expect fun formatTransactionDate(date: LocalDateTime, outputFormat: String = "dd MMM yyyy, h:mm a"): String