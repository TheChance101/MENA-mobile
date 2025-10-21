package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toUiColor SHOULD map hex string to ARGB long`() {
        val color = Color(id = uuid, hexCode = "#FF0000") // red
        val uiColor = color.toUiColor()

        assertEquals(uuid.toString(), uiColor.id)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toEntity SHOULD map ColorUiState back to domain Color`() {
        val uiColor = CreateDukanUiState.ColorUiState(id = uuid.toString(), color = 0xFF00FF00) // green
        val color = uiColor.toEntity()

        assertEquals(uuid, color.id)
        assertEquals("#FF00FF00", color.hexCode) // ARGB in hex string
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toUiState SHOULD map Category list to DukanCategoryUiState list`() {
        val categories = listOf(Category( uuid, "Fruits", "fruits.png"))
        val uiList = categories.toUiState()

        assertEquals(1, uiList.size)
        assertEquals(uuid.toString(), uiList[0].id)
        assertEquals("Fruits", uiList[0].name)
        assertEquals("fruits.png", uiList[0].imageUrl)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toEntity SHOULD map DukanCategoryUiState to Category`() {
        val uiState = CreateDukanUiState.DukanCategoryUiState(uuid.toString(), "Vegetables", "veg.png")
        val category = uiState.toEntity()

        assertEquals(uuid, category.id)
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

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toEntity SHOULD map categories correctly`() {
        val uiState = createDukanUiState()
        val dukan = uiState.toEntity()
        assertTrue(dukan.categories.any { it.id == uuid && it.name == "Fruits" })
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

    @OptIn(ExperimentalUuidApi::class)
    private fun createDukanUiState() = CreateDukanUiState(
        name = "My Dukan",
        selectedCategories = setOf(CreateDukanUiState.DukanCategoryUiState( uuid.toString(), "Fruits", "f.png")),
        currentLocation = CreateDukanUiState.CoordinatesUiState(10.0, 20.0),
        address = "Cairo",
        selectedColor = CreateDukanUiState.ColorUiState(uuid.toString(), 0xFF112233),
        selectedStyle = CreateDukanUiState.Style.NO_IMAGE
    )
    private companion object{
        @OptIn(ExperimentalUuidApi::class)
        val uuid =  Uuid.parse("550e8400-e29b-41d4-a716-446655440000")
    }
}