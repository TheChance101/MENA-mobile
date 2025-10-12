package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.CreateShelfRequest
import net.thechance.mena.dukan.data.repository.dto.ShelfDto
import net.thechance.mena.dukan.domain.entity.Shelf

fun Shelf.toCreateShelfRequest(): CreateShelfRequest {
    return CreateShelfRequest(title = name)
}

fun ShelfDto.toShelf(): Shelf {
    return Shelf(
        id = id,
        name = title
    )
}
