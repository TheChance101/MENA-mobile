package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createDukanDiscoveryRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultBestAroundResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultDukansByCategoryResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultEditorPicksResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultTopDiscountedResponse
import org.junit.Test
import kotlin.test.assertEquals

class DukanDiscoveryRepositoryImplTest {

    @Test
    fun `getEditorPicksDukans should return paged result with dukans`() = runTest {
        val repo = createDukanDiscoveryRepository(
            editorPicksDukansResponse = { defaultEditorPicksResponse() }
        )

        val result = repo.getEditorPicksDukans(page = 0, size = 2)

        assertEquals(2, result.items.size)
    }

    @Test
    fun `getBestAroundDukans return paged result with dukans`() = runTest {
        val repo = createDukanDiscoveryRepository(
            neastAroundDukansResponse = { defaultBestAroundResponse() }
        )

        val result = repo.getBestAroundDukans(page = 0, size = 2)

        assertEquals(2, result.items.size)
    }

    @Test
    fun `getDukansByCategory return paged result with dukans`() = runTest {
        val repo = createDukanDiscoveryRepository(
            dukanByCategoriesResponse = { defaultDukansByCategoryResponse() }
        )

        val categoryId = "20"
        val result = repo.getDukansByCategory(categoryId = categoryId, page = 0, size = 2)

        assertEquals(2, result.items.size)
    }

    @Test
    fun `getTopDiscountedDukans should return paged result with discounted dukans`() = runTest {
        val repo = createDukanDiscoveryRepository(
            topDiscountedDukansResponse = { defaultTopDiscountedResponse() }
        )

        val result = repo.getTopDiscountedDukans(page = 0, size = 2)

        assertEquals(2, result.items.size)
    }

}
