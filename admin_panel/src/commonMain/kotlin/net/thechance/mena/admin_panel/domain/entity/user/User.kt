@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.admin_panel.domain.entity.user

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid
)
