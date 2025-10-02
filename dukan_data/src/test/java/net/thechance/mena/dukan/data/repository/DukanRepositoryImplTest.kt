package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createDukanRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultCreateResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultNameAvailableResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.defaultStatusResponse
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DukanRepositoryImplTest {
    private val repository: DukanRepositoryImpl = createDukanRepository()

    @Test
    fun `createDukan calls the correct endpoint`() = runTest {
        var called = false
        val repo = createDukanRepository(
            createResponse = {
                called = true
                defaultCreateResponse()
            }
        )

        repo.createDukan(
            Dukan(
                id = "123",
                name = "Test Dukan",
                color = Color("Red", "#FF0000"),
                style = Dukan.Style.WIDE_IMAGE,
                imageUrl = "",
                categories = emptySet<Category>(),
                coordinates = Dukan.Coordinates(0.0, 0.0),
                address = "",
                status = Dukan.Status.PENDING
            )
        )

        assertTrue(called)
    }

    @Test
    fun `getMyDukanStatus returns mapped status`() = runTest {
        val repo = createDukanRepository(
            statusResponse = { defaultStatusResponse() }
        )

        val status = repo.getMyDukanStatus()

        assertEquals(
            MyDukanStatus(Dukan.Status.PENDING, "Active"),
            status
        )
    }

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