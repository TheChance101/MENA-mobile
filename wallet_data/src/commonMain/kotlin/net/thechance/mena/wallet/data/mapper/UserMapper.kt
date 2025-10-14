package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.ReceiverDto
import net.thechance.mena.wallet.domain.model.Receiver
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ReceiverDto.toEntity() = Receiver(
    name = name ?: "",
    imgUrl = imageUrl
)