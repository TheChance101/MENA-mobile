package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.ReciterDto
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

fun ReciterDto.toDomain(): Reciter {
    return Reciter(
        id = id,
        name = name,
        arabicName = nameAr,
        tilawahType = tilawahType
    )
}

fun Reciter.toDomain(): ReciterDto {
    return ReciterDto(
        id = id,
        name = name,
        nameAr = arabicName,
        tilawahType = tilawahType
    )
}