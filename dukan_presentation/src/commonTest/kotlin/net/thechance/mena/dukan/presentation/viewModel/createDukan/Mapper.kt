package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateDukanMapperTest {

    @Test
    fun `toUiStyleName SHOULD return correct name for WIDE_IMAGE`() {
        assertEquals("Wide image with list products", Dukan.Style.WIDE_IMAGE.toUiStyleName())
    }

    @Test
    fun `toUiStyleName SHOULD return correct name for SMALL_IMAGE`() {
        assertEquals("Small image with grid products", Dukan.Style.SMALL_IMAGE.toUiStyleName())
    }

    @Test
    fun `toUiStyleName SHOULD return correct name for NO_IMAGE`() {
        assertEquals("No dukan image", Dukan.Style.NO_IMAGE.toUiStyleName())
    }

    @Test
    fun `toUiColor SHOULD map hex string to ARGB long`() {
        val color = Color(id = "1", hexCode = "#FF0000") // red
        val uiColor = color.toUiColor()

        assertEquals("1", uiColor.id)
    }

    @Test
    fun `toEntity SHOULD map ColorUiState back to domain Color`() {
        val uiColor = CreateDukanUiState.ColorUiState(id = "2", color = 0xFF00FF00) // green
        val color = uiColor.toEntity()

        assertEquals("2", color.id)
        assertEquals("#FF00FF00", color.hexCode) // ARGB in hex string
    }

    @Test
    fun `toUiState SHOULD map Category list to DukanCategoryUiState list`() {
        val categories = listOf(Category("10", "Fruits", "fruits.png"))
        val uiList = categories.toUiState()

        assertEquals(1, uiList.size)
        assertEquals("10", uiList[0].id)
        assertEquals("Fruits", uiList[0].name)
        assertEquals("fruits.png", uiList[0].imageUrl)
    }

    @Test
    fun `toEntity SHOULD map DukanCategoryUiState to Category`() {
        val uiState = CreateDukanUiState.DukanCategoryUiState("11", "Vegetables", "veg.png")
        val category = uiState.toEntity()

        assertEquals("11", category.id)
        assertEquals("Vegetables", category.name)
        assertEquals("veg.png", category.imageUrl)
    }
    @Test
    fun `toEntity SHOULD map name correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals("My Dukan", dukan.name)
    }

    @Test
    fun `toEntity SHOULD map address correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals("Cairo", dukan.address)
    }

    @Test
    fun `toEntity SHOULD map categories correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertTrue(dukan.categories.any { it.id == "1" && it.name == "Fruits" })
    }

    @Test
    fun `toEntity SHOULD map coordinates correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals(10.0, dukan.coordinates.latitude)
        assertEquals(20.0, dukan.coordinates.longitude)
    }

    @Test
    fun `toEntity SHOULD map color correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals("#FF112233", dukan.color.hexCode)
    }

    @Test
    fun `toEntity SHOULD map style correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals(Dukan.Style.NO_IMAGE, dukan.style)
    }

    @Test
    fun `toEntity SHOULD always set status to PENDING`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertEquals(Dukan.Status.PENDING, dukan.status)
    }

    @Test
    fun `toEntity SHOULD map CoordinatesUiState to Coordinates`() {
        val uiCoordinates = CreateDukanUiState.CoordinatesUiState(5.5, 6.6)
        val coords = uiCoordinates.toEntity()

        assertEquals(5.5, coords.latitude)
        assertEquals(6.6, coords.longitude)
    }

    private fun createDukanUiState() = CreateDukanUiState(
        name = "My Dukan",
        selectedCategories = setOf(CreateDukanUiState.DukanCategoryUiState("1", "Fruits", "f.png")),
        currentLocation = CreateDukanUiState.CoordinatesUiState(10.0, 20.0),
        address = "Cairo",
        selectedColor = CreateDukanUiState.ColorUiState("c1", 0xFF112233),
        selectedStyle = CreateDukanUiState.Style.NO_IMAGE
    )
}