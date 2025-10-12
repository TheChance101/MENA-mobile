package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.UserDto
import net.thechance.mena.wallet.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun UserDto.toEntity(id: Uuid) = User(
    id = id,
    name = name ?: "",
    imgUrl = imageUrl
)