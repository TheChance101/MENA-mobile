package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Color @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val hexCode: String,
)