package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.ShelfResponse
import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals

class ShelfMappersTest {

    @Test
    fun `Shelf to CreateShelfRequest maps correctly`() {
        // Given
        val shelf = fakeShelf()

        // When
        val request = shelf.toCreateShelfRequest()

        // Then
        assertEquals("Test Shelf", request.title)
    }

    @Test
    fun `ShelfResponse to Shelf maps id correctly`() {
        // Given
        val shelfResponse = fakeShelfResponse()

        // When
        val shelf = shelfResponse.toShelf()

        // Then
        assertEquals("1", shelf.id)
    }

    @Test
    fun `ShelfResponse to Shelf maps name correctly`() {
        // Given
        val shelfResponse = fakeShelfResponse()

        // When
        val shelf = shelfResponse.toShelf()

        // Then
        assertEquals("Shelf 1", shelf.name)
    }


    @Test
    fun `List of ShelfResponse to ShelfList maps size correctly`() {
        // Given
        val shelfResponses = fakeShelfResponses()

        // When
        val shelves = shelfResponses.toShelfList()

        // Then
        assertEquals(3, shelves.size)
    }

    @Test
    fun `List of ShelfResponse to ShelfList maps first shelf id correctly`() {
        // Given
        val shelfResponses = fakeShelfResponses()

        // When
        val shelves = shelfResponses.toShelfList()

        // Then
        assertEquals("1", shelves[0].id)
    }

    @Test
    fun `List of ShelfResponse to ShelfList maps first shelf name correctly`() {
        // Given
        val shelfResponses = fakeShelfResponses()

        // When
        val shelves = shelfResponses.toShelfList()

        // Then
        assertEquals("Shelf 1", shelves[0].name)
    }


    @Test
    fun `List of ShelfResponse to ShelfList maps empty list correctly`() {
        // Given
        val shelfResponses = emptyList<ShelfResponse>()

        // When
        val shelves = shelfResponses.toShelfList()

        // Then
        assertEquals(0, shelves.size)
    }
}

private fun fakeShelf() = Shelf(
    id = "123",
    name = "Test Shelf"
)

private fun fakeShelfResponse() = ShelfResponse(
    id = "1",
    title = "Shelf 1"
)

private fun fakeShelfResponses() = listOf(
    ShelfResponse(id = "1", title = "Shelf 1"),
    ShelfResponse(id = "2", title = "Shelf 2"),
    ShelfResponse(id = "3", title = "Shelf 3")
)
