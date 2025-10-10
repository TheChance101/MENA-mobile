package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDateTime


expect fun formatLocalDateTime(date: LocalDateTime, outputFormat: String): String