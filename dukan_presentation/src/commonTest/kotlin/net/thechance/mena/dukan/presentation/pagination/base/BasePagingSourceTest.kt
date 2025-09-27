package net.thechance.mena.dukan.presentation.pagination.base

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.presentation.util.pagination.PagedFetchResponse
import net.thechance.mena.dukan.presentation.util.pagination.PagingSource
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BasePagingSourceTest {

    @Test
    fun `when load initial should call FIRST_PAGE`() = runTest {
        var page: Int? = null
        val source = object : BasePagingSource<String>() {
            override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<String> {
                page = pageNumber
                return fakeResponse()
            }
        }

        source.load(PagingSource.LoadParams(null, 10))
        assertEquals(BasePagingSource.FIRST_PAGE, page)
    }

    @Test
    fun `when load with specific key then onFetchPage called with that key`() = runTest {
        var calledPage: Int? = null
        val source = object : BasePagingSource<String>() {
            override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<String> {
                calledPage = pageNumber
                return fakeResponse(currentPage = pageNumber)
            }
        }

        source.load(PagingSource.LoadParams(7, 10))

        assertEquals(7, calledPage)
    }

    @Test
    fun `when load page with key the previous key should less than one`() = runTest {
        val source = object : BasePagingSource<String>() {
            override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<String> {
                return fakeResponse(
                    currentPage = pageNumber,
                    hasPrevious = true,
                    hasNext = false
                )
            }
        }

        val result = source.load(PagingSource.LoadParams(5, 10))

        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(4, result.prevKey)
    }

    @Test
    fun `when load page with key the next key should greater than one`() = runTest {
        val source = object : BasePagingSource<String>() {
            override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<String> {
                return fakeResponse(
                    currentPage = pageNumber,
                    hasPrevious = false,
                    hasNext = true
                )
            }
        }

        val result = source.load(PagingSource.LoadParams(5, 10))

        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(6, result.nextKey)
    }

    @Test
    fun `when onFetchPage throws then return Error`() = runTest {
        val source = object : BasePagingSource<String>() {
            override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<String> {
                throw IllegalStateException("failed")
            }
        }

        val result = source.load(PagingSource.LoadParams(1, 10))

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals("failed", result.throwable.message)
    }

    private fun fakeResponse(
        items: List<String> = listOf("A", "B", "C"),
        hasPrevious: Boolean = false,
        hasNext: Boolean = false,
        currentPage: Int = 1,
        totalPages: Int = 1
    ) = PagedFetchResponse(
        items = items,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        currentPage = currentPage,
        totalPages = totalPages
    )
}
