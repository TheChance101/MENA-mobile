package net.thechance.mena.core_chat.presentation.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun getUuidOrNull(input: String?): Uuid? =
    runCatching { Uuid.parse(input.orEmpty()) }.getOrNull()