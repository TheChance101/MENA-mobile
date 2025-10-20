package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.shelf.CreateShelfRequest
import net.thechance.mena.dukan.data.dto.shelf.ShelfDto
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
