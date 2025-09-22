package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.DukanCategoryDto
import net.thechance.mena.dukan.data.repository.dto.DukanColorDto
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import org.junit.Test
import kotlin.test.assertEquals

class DukanMappersTest {

    @Test
    fun `Dukan to CreateDukanRequest maps correctly`() {
        val dukan = Dukan(
            id = "1",
            name = "My Dukan",
            categories = setOf(Category("cat1", "Category 1", "")),
            address = "Baghdad",
            coordinates = Dukan.Coordinates(33.3, 44.4),
            color = Color("color1", "#FFFFFF"),
            style = Dukan.Style.WIDE_IMAGE,
            imageUrl = "",
            status = Dukan.Status.PENDING,
        )

        val request = dukan.toCreateDukanRequest()

        assertEquals("My Dukan", request.name)
        assertEquals(setOf("cat1"), request.categoryIds)
        assertEquals("Baghdad", request.address)
        assertEquals(33.3, request.latitude)
        assertEquals(44.4, request.longitude)
        assertEquals("color1", request.colorId)
        assertEquals("WIDE_IMAGE", request.style)
    }

    @Test
    fun `List of DukanCategoryDto toCategoryList maps correctly`() {
        val dtos = listOf(
            DukanCategoryDto("1", "Food", "food.png"),
            DukanCategoryDto("2", "Clothes", "clothes.png")
        )

        val categories = dtos.toCategoryList()

        assertEquals(2, categories.size)
        assertEquals("1", categories[0].id)
        assertEquals("Food", categories[0].name)
        assertEquals("food.png", categories[0].imageUrl)
        assertEquals("2", categories[1].id)
        assertEquals("Clothes", categories[1].name)
        assertEquals("clothes.png", categories[1].imageUrl)
    }

    @Test
    fun `List of DukanColorDto toColorsList maps correctly`() {
        val dtos = listOf(
            DukanColorDto("red", "#FF0000"),
            DukanColorDto("green", "#00FF00")
        )

        val colors = dtos.toColorsList()

        assertEquals(2, colors.size)
        assertEquals("red", colors[0].id)
        assertEquals("#FF0000", colors[0].hexCode)
        assertEquals("green", colors[1].id)
        assertEquals("#00FF00", colors[1].hexCode)
    }

    @Test
    fun `MyDukanStatusDto toCategoryList maps correctly`() {
        val dto = MyDukanStatusDto(
            status = "PENDING",
            dukanName = "My Dukan"
        )

        val status = dto.toMyDukanStatus()

        assertEquals(Dukan.Status.PENDING, status.status)
        assertEquals("My Dukan", status.dukanName)
    }
}