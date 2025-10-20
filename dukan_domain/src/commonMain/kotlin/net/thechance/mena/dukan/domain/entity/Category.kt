package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Category @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val name: String,
    val imageUrl: String
)
