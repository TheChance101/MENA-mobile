package net.thechance.mena.admin_panel.domain.entity.dukan

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Shelf(
    val id: Uuid,
    val title: String
)