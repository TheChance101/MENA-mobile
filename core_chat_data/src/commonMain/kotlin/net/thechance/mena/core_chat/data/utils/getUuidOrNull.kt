package net.thechance.mena.core_chat.data.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun getUuidOrNull(input: String?): Uuid? =
    runCatching { Uuid.parse(input.orEmpty()) }.getOrNull()

@OptIn(ExperimentalUuidApi::class)
fun String.toUuid(): Uuid = Uuid.parse(this)