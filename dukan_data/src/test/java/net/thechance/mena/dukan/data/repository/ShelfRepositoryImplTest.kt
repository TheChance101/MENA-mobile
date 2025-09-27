package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class ShelfRepositoryImplTest {
    private val repository = createShelfRepository()

    @Test
    fun `createShelf calls the correct endpoint`() = runTest {
        var called = false
        val repo = createShelfRepository(
            createResponse = {
                called = true
                defaultCreateResponse()
            }
        )
        repo.createShelf(
            Shelf(
                id = "123",
                name = "Test Shelf",
                dukanId = "123"
            )
        )
        assertTrue(called)
    }

    @Test
    fun `deleteShelf call success when return status code between 200 to 299`() = runTest {
        val shelfId = "1"
        val repo = shelfRepository(
            deleteShelfResponse = {
                defaultDeleteShelfResponse()
            }
        )

        val result = repo.deleteShelf(shelfId)

        assertEquals(
            expected = true,
            actual = result,
        )
    }

}