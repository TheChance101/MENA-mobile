package net.thechance.mena.wallet.data.mapper

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun String.toUuidOrNull() = runCatching { Uuid.parse(this) }.getOrNull()