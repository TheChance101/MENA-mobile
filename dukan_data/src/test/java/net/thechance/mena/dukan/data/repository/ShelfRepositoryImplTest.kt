package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ShelfRepositoryImplTest {

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