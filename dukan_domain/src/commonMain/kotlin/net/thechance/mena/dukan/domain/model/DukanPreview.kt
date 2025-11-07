@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class DukanPreview(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val isFavorite: Boolean,
)
