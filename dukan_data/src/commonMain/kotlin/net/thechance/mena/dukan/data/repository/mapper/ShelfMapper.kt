package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.CreateShelfRequest
import net.thechance.mena.dukan.data.repository.dto.ShelfResponse
import net.thechance.mena.dukan.domain.entity.Shelf

fun Shelf.toCreateShelfRequest(): CreateShelfRequest {
    return CreateShelfRequest(title = name)
}

fun ShelfResponse.toShelf(): Shelf {
    return Shelf(
        id = id,
        name = title
    )
}

fun List<ShelfResponse>.toShelfList(): List<Shelf> {
    return map { it.toShelf() }
}
