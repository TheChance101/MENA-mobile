package net.thechance.mena.faith.data.mapper.mosque

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto
import net.thechance.mena.faith.domain.entity.Mosque

@OptIn(ExperimentalUuidApi::class)
fun MosqueDto.toDomain(): Mosque = Mosque(
    id = Uuid.parse(id),
    name = name,
    address = address,
    imageUrl = imageUrl,
    coordinates = Mosque.Coordinates(
        latitude = latitude,
        longitude = longitude
    )
)

fun List<MosqueDto>.toDomain(): List<Mosque> = map { it.toDomain() }
