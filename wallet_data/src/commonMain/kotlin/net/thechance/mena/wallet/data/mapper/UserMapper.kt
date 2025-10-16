package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.TransactionReceiverDto
import net.thechance.mena.wallet.domain.model.TransactionReceiver
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun TransactionReceiverDto.toEntity() = TransactionReceiver(
    name = name ?: "",
    imgUrl = imageUrl
)