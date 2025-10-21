package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.dto.shelf.ShelfDto
import net.thechance.mena.dukan.data.mapper.toCreateShelfRequest
import net.thechance.mena.dukan.data.mapper.toShelf
import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ShelfMappersTest {

    @Test
    fun `Shelf to CreateShelfRequest maps correctly`() {
        val shelf = fakeShelf()
        val request = shelf.toCreateShelfRequest()
        assertEquals("Test Shelf", request.title)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `ShelfResponse to Shelf maps id correctly`() {
        val shelfResponse = fakeShelfResponse()
        val shelf = shelfResponse.toShelf()
        assertEquals(shelfResponse.id, shelf.id)
    }

    @Test
    fun `ShelfResponse to Shelf maps name correctly`() {
        val shelfResponse = fakeShelfResponse()
        val shelf = shelfResponse.toShelf()
        assertEquals(shelfResponse.title, shelf.name)
    }

    @Test
    fun `List of ShelfResponse to ShelfList maps size correctly`() {
        val shelfResponses = fakeShelfResponses()
        val shelves = shelfResponses.map { it.toShelf() }
        assertEquals(3, shelves.size)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `List of ShelfResponse to ShelfList maps first shelf id correctly`() {
        val shelfResponses = fakeShelfResponses()
        val shelves = shelfResponses.map { it.toShelf() }
        assertEquals(shelfResponses.first().id, shelves.first().id)
    }

    @Test
    fun `List of ShelfResponse to ShelfList maps first shelf name correctly`() {
        val shelfResponses = fakeShelfResponses()
        val shelves = shelfResponses.map { it.toShelf() }
        assertEquals(shelfResponses.first().title, shelves.first().name)
    }

    @Test
    fun `List of ShelfResponse to ShelfList maps empty list correctly`() {
        val shelfResponses = emptyList<ShelfDto>()
        val shelves = shelfResponses.map { it.toShelf() }
        assertEquals(0, shelves.size)
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun fakeShelf() = Shelf(
    id = Uuid.random(),
    name = "Test Shelf"
)

@OptIn(ExperimentalUuidApi::class)
private fun fakeShelfResponse() = ShelfDto(
    id = Uuid.random(),
    title = "Shelf 1"
)

@OptIn(ExperimentalUuidApi::class)
private fun fakeShelfResponses() = listOf(
    ShelfDto(id = Uuid.random(), title = "Shelf 1"),
    ShelfDto(id = Uuid.random(), title = "Shelf 2"),
    ShelfDto(id = Uuid.random(), title = "Shelf 3")
)
