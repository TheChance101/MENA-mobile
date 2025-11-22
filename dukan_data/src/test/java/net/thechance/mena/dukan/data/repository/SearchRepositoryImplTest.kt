package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.search.createSearchRepository
import org.junit.Test


class SearchRepositoryImplTest {

    @Test
    fun `findDukansByQuery returns correct data size`() = runTest {
        val repository = createSearchRepository()

        val result = repository.findDukansByQuery("query", 0, 10)

        assert(result.items.size == 2)
    }

    @Test
    fun `find dukans by query in category returns correct data size`() = runTest{
        val repository = createSearchRepository()

        val result = repository.finDukansByQueryInCategory("1", "query", 0, 10)

        assert(result.items.size == 2)
    }

    @Test
    fun `findProductsByQuery returns correct data size`() = runTest {
        val repository = createSearchRepository()

        val result = repository.findProductsByQuery("query", 0, 10)

        assert(result.items.size == 2)

    }
}