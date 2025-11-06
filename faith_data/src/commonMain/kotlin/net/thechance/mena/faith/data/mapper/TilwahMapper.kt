package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest
import net.thechance.mena.faith.domain.model.Reciter

fun RecitersRequest.toDomain(): Reciter {
    return Reciter(
        id = id,
        name = name,
        arabicName = arabicName,
        tilawahType = tilawahType
    )
}