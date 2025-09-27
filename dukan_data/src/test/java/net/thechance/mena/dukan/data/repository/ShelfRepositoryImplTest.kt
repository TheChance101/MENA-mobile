package net.thechance.mena.dukan.data.repository

import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ShelfRepositoryImplTest {
    private val repository = createShelfRepository()

    @Test
    fun `createShelf calls the correct endpoint`() = runTest {
        // Given
        var called = false
        val repo = createShelfRepository(
            createResponse = {
                called = true
                defaultCreateResponse()
            }
        )

        // When
        repo.createShelf(fakeShelf())

        // Then
        assertTrue(called)
    }

    @Test
    fun `createShelf sends correct request body`() = runTest {
        // Given
        val testShelf = fakeShelf()
        var requestBody: String? = null
        val repo = createShelfRepository(
            createResponse = {
                requestBody = "Test Shelf"
                defaultCreateResponse()
            }
        )

        // When
        repo.createShelf(testShelf)

        // Then
        assertTrue(requestBody?.contains("Test Shelf") == true)
    }

    @Test
    fun `createShelf handles error response`() = runTest {
        // Given
        val testShelf = fakeShelf()
        val repo = createShelfRepository(
            createResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repo.createShelf(testShelf)
        }
    }

    @Test
    fun `getMyDukanShelves calls the correct endpoint`() = runTest {
        // Given
        var called = false
        val repo = createShelfRepository(
            shelvesResponse = {
                called = true
                defaultShelvesResponse()
            }
        )

        // When
        repo.getMyDukanShelves()

        // Then
        assertTrue(called)
    }

    @Test
    fun `getMyDukanShelves returns correct data size`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repo.getMyDukanShelves()

        // Then
        assertEquals(3, shelves.size)
    }

    @Test
    fun `getMyDukanShelves returns correct first shelf id`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repo.getMyDukanShelves()

        // Then
        assertEquals("1", shelves[0].id)
    }

    @Test
    fun `getMyDukanShelves returns correct first shelf name`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repo.getMyDukanShelves()

        // Then
        assertEquals("Shelf 1", shelves[0].name)
    }

    @Test
    fun `getMyDukanShelves returns correct first shelf dukanId`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repo.getMyDukanShelves()

        // Then
        assertEquals("123", shelves[0].dukanId)
    }

    @Test
    fun `getMyDukanShelves handles empty response`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                respond("[]", HttpStatusCode.OK, jsonHeaders)
            }
        )

        // When
        val shelves = repo.getMyDukanShelves()

        // Then
        assertEquals(0, shelves.size)
    }

    @Test
    fun `getMyDukanShelves handles error response`() = runTest {
        // Given
        val repo = createShelfRepository(
            shelvesResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repo.getMyDukanShelves()
        }
    }

}

private fun fakeShelf() = Shelf(
    id = "123",
    name = "Test Shelf",
    dukanId = "123"
)
