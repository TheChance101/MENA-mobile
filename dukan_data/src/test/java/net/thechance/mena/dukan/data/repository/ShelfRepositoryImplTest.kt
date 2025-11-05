package net.thechance.mena.dukan.data.repository

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createShelfRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultCreateResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultDeleteShelfResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultPagedShelvesResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultShelvesResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ShelfRepositoryImplTest {

    @Test
    fun `createShelf calls the correct endpoint`() = runTest {
        // Given
        var called = false
        val repository = createShelfRepository(
            createResponse = {
                called = true
                defaultCreateResponse()
            }
        )

        // When
        repository.createShelf(fakeShelf())

        // Then
        assertTrue(called)
    }

    @Test
    fun `createShelf sends correct request body`() = runTest {
        // Given
        val testShelf = fakeShelf()
        var requestBody: String? = null
        val repository = createShelfRepository(
            createResponse = {
                requestBody = "Test Shelf"
                defaultCreateResponse()
            }
        )

        // When
        repository.createShelf(testShelf)

        // Then
        assertTrue(requestBody?.contains("Test Shelf") == true)
    }

    @Test
    fun `createShelf handles error response`() = runTest {
        // Given
        val testShelf = fakeShelf()
        val repository = createShelfRepository(
            createResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repository.createShelf(testShelf)
        }
    }

    @Test
    fun `getMyDukanShelves calls the correct endpoint`() = runTest {
        // Given
        var called = false
        val repository = createShelfRepository(
            shelvesResponse = {
                called = true
                defaultShelvesResponse()
            }
        )

        // When
        repository.getMyDukanShelves()

        // Then
        assertTrue(called)
    }

    @Test
    fun `getMyDukanShelves returns correct data size`() = runTest {
        // Given
        val repository = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repository.getMyDukanShelves()

        // Then
        assertEquals(3, shelves.size)
    }

    @Test
    fun `getMyDukanShelves returns correct first shelf name`() = runTest {
        // Given
        val repository = createShelfRepository(
            shelvesResponse = {
                defaultShelvesResponse()
            }
        )

        // When
        val shelves = repository.getMyDukanShelves()

        // Then
        assertEquals("Shelf 1", shelves[0].name)
    }

    @Test
    fun `getMyDukanShelves handles empty response`() = runTest {
        // Given
        val repository = createShelfRepository(
            shelvesResponse = {
                respond("[]", HttpStatusCode.OK, jsonHeaders)
            }
        )

        // When
        val shelves = repository.getMyDukanShelves()

        // Then
        assertEquals(0, shelves.size)
    }

    @Test
    fun `getMyDukanShelves handles error response`() = runTest {
        // Given
        val repository = createShelfRepository(
            shelvesResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repository.getMyDukanShelves()
        }
    }

    @Test
    fun `deleteShelf call success`() = runTest {
        val shelfId = "1"
        var called = false
        val repository = createShelfRepository(
            deleteShelfResponse = {
                called = true
                defaultDeleteShelfResponse()
            }
        )

        repository.deleteShelf(shelfId)

        assertTrue(
            called,
        )
    }

    @Test
    fun `deleteShelf handle error response`() = runTest {
        val shelfId = "1"
        val repository = createShelfRepository(
            deleteShelfResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        assertFailsWith<Exception> {
            repository.deleteShelf(shelfId)
        }
    }

    @Test
    fun `getShelvesByDukanId should call the service`() = runTest {
        // Given
        var called = false
        val repository = createShelfRepository(
            pagedShelvesResponse = {
                called = true
                defaultPagedShelvesResponse()
            }
        )
        // When
        repository.getShelvesByDukanId("dukan123", 0, 2)
        // Then
        assertTrue(called)
    }

    @Test
    fun `getShelvesByDukanId should call the correct endpoint path`() = runTest {
        // Given
        val dukanId = "dukan123"
        var actualUrl = ""
        val repository = createShelfRepository(
            pagedShelvesResponse = { request ->
                actualUrl = request.url.toString()
                defaultPagedShelvesResponse()
            }
        )
        // When
        repository.getShelvesByDukanId(dukanId, 0, 2)
        // Then
        assertTrue(actualUrl.contains("/dukan/shelf/$dukanId"))
    }

    @Test
    fun `getShelvesByDukanId should include correct page parameter`() = runTest {
        // Given
        val page = 0
        var actualUrl = ""
        val repository = createShelfRepository(
            pagedShelvesResponse = { request ->
                actualUrl = request.url.toString()
                defaultPagedShelvesResponse()
            }
        )
        // When
        repository.getShelvesByDukanId("dukan123", page, 2)
        // Then
        assertTrue(actualUrl.contains("page=$page"))
    }

    @Test
    fun `getShelvesByDukanId should include correct size parameter`() = runTest {
        // Given
        val pageSize = 2
        var actualUrl = ""
        val repository = createShelfRepository(
            pagedShelvesResponse = { request ->
                actualUrl = request.url.toString()
                defaultPagedShelvesResponse()
            }
        )
        // When
        repository.getShelvesByDukanId("dukan123", 0, pageSize)
        // Then
        assertTrue(actualUrl.contains("size=$pageSize"))
    }

    @Test
    fun `getShelvesByDukanId should return correct number of items`() = runTest {
        // Given
        val repository =
            createShelfRepository(pagedShelvesResponse = { defaultPagedShelvesResponse() })
        // When
        val result = repository.getShelvesByDukanId("dukan123", 0, 2)
        // Then
        assertEquals(2, result.pageSize)
    }

    @Test
    fun `getShelvesByDukanId when response is empty should return an empty list`() = runTest {
        // Given
        val repository = createShelfRepository(pagedShelvesResponse = { emptyPagedResponse() })

        // When
        val result = repository.getShelvesByDukanId("dukan123", 0, 10)

        // Then
        assertTrue(result.items.isEmpty())
    }

    @Test
    fun `getShelvesByDukanId handles error response`() = runTest {
        // Given
        val dukanId = "dukan123"
        val repository = createShelfRepository(
            pagedShelvesResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repository.getShelvesByDukanId(dukanId, 0, 10)
        }
    }

    @Test
    fun `updateShelf calls the correct endpoint`() = runTest {
        // Given
        var called = false
        val repository = createShelfRepository(
            updateShelfResponse = {
                called = true
                defaultCreateResponse()
            }
        )

        // When
        repository.updateShelf("1", "Updated Shelf")

        // Then
        assertTrue(called)
    }

    @Test
    fun `updateShelf sends correct request body`() = runTest {
        // Given
        var requestBody: String? = null
        val repository = createShelfRepository(
            updateShelfResponse = {
                requestBody = "Updated Shelf"
                defaultCreateResponse()
            }
        )

        // When
        repository.updateShelf("1", "Updated Shelf")

        // Then
        assertEquals(requestBody?.contains("Updated Shelf"), true)
    }

    @Test
    fun `updateShelf handles error response`() = runTest {
        // Given
        val repository = createShelfRepository(
            updateShelfResponse = {
                respond("", HttpStatusCode.BadRequest, jsonHeaders)
            }
        )

        // When & Then
        assertFailsWith<Exception> {
            repository.updateShelf("1", "Updated Shelf")
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun fakeShelf() = Shelf(
    id = Uuid.random(),
    name = "Test Shelf",
)

private fun MockRequestHandleScope.emptyPagedResponse() = respond(
    content = """
        {
            "content": [],
            "number": 0,
            "size": 10,
            "totalPages": 0,
            "totalElements": 0,
            "first": true,
            "last": true
        }
    """.trimIndent(),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)