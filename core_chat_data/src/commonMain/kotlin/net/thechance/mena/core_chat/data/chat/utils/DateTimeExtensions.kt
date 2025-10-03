@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Instant.toLocalDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())
fun LocalDateTime.toInstant() = toInstant(TimeZone.currentSystemDefault())