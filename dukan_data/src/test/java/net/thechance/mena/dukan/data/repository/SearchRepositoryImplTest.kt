package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.search.defaultDukanSearchResponse
import net.thechance.mena.dukan.data.repository.mockEngine.search.defaultProductSearchResponse
import net.thechance.mena.dukan.data.repository.mockEngine.search.searchRepository
import org.junit.Assert.assertTrue
import org.junit.Test


class SearchRepositoryImplTest {
    @Test
    fun `test dukan search is called`() = runTest {
        var called = false

        val repo = searchRepository(
            dukanSearchResponse = {
                called = true
                defaultDukanSearchResponse()
            }
        )

        repo.findDukansByQuery("defacto", 0, 10)

        assertTrue(called)
    }

    @Test
    fun `test product search is called`() = runTest {
        var called = false

        val repo = searchRepository(
            productSearchResponse = {
                called = true
                defaultProductSearchResponse()
            }
        )

        repo.findProductsByQuery("perfume", 0, 10)

        assertTrue(called)
    }
}


