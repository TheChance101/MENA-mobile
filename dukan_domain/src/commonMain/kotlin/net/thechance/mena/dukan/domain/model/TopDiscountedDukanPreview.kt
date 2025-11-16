@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TopDiscountedDukanPreview(
    val id : Uuid,
    val imageUrl : String,
    val discount : Int,
)