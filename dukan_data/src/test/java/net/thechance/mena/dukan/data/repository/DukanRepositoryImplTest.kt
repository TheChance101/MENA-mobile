package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DukanRepositoryImplTest {
    private val repository = createDukanRepository()

    @Test
    fun `getCategories returns mapped categories`() = runTest {
        val categories = repository.getCategories()
        assertEquals(
            listOf(
                Category("1", "Category 1", ""),
                Category("2", "Category 2", ""),
                Category("3", "Category 3", "")
            ),
            categories
        )
    }

    @Test
    fun `getDukanColors returns mapped colors`() = runTest {
        val colors = repository.getDukanColors()
        assertEquals(
            listOf(
                Color("Red", "#FF0000"),
                Color("Green", "#00FF00"),
                Color("Blue", "#0000FF")
            ),
            colors
        )
    }

    @Test
    fun `getDukanStyles returns styles as enums`() = runTest {
        val styles = repository.getDukanStyles()
        assertEquals(
            listOf(Dukan.Style.WIDE_IMAGE, Dukan.Style.NO_IMAGE),
            styles
        )
    }

    @Test
    fun `isDukanNameTaken returns false for available name`() = runTest {
        val isTaken = repository.isDukanNameTaken("some_name")
        assertFalse(isTaken)
    }

    @Test
    fun `isDukanNameTaken returns true for taken name`() = runTest {
        val repo = createDukanRepository(
            nameResponse = { defaultNameAvailableResponse(isTaken = true) }
        )
        val isTaken = repo.isDukanNameTaken("taken_name")
        assertTrue(isTaken)
    }

    @Test
    fun `uploadDukanImage returns image URL`() = runTest {
        val url = repository.uploadDukanImage(
            fileName = "image.png",
            fileBytes = ByteArray(0)
        )
        assertEquals("https://cdn.example.com/dukan/image.png", url)
    }
}