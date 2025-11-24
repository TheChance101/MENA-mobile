package net.thechance.mena.dukan.data.repository.mapper

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.dto.dukan.DukanActivationStatusResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanCategoryDto
import net.thechance.mena.dukan.data.dto.dukan.DukanColorDto
import net.thechance.mena.dukan.data.dto.dukan.DukanDetailsDto
import net.thechance.mena.dukan.data.dto.dukan.MyDukanStatusDto
import net.thechance.mena.dukan.data.dto.dukan.TopDiscountedDukanDto
import net.thechance.mena.dukan.data.mapper.toActivationStatus
import net.thechance.mena.dukan.data.mapper.toCategoryList
import net.thechance.mena.dukan.data.mapper.toColorsList
import net.thechance.mena.dukan.data.mapper.toCreateDukanRequest
import net.thechance.mena.dukan.data.mapper.toDukan
import net.thechance.mena.dukan.data.mapper.toEntity
import net.thechance.mena.dukan.data.mapper.toMyDukanStatus
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DukanMappersTest {

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `Dukan to CreateDukanRequest maps correctly`() {
        val dukanId = Uuid.random()
        val categoryId = Uuid.random()
        val colorId = Uuid.random()
        val dukan = Dukan(
            id = dukanId,
            name = "My Dukan",
            isFavorite = false,
            categories = setOf(Category(categoryId, "Category 1", "")),
            address = "Baghdad",
            coordinates = Dukan.Coordinates(33.3, 44.4),
            color = Color(colorId, "#FFFFFF"),
            style = Dukan.Style.WIDE_IMAGE,
            imageUrl = "",
            status = Dukan.Status.PENDING,
        )

        val request = dukan.toCreateDukanRequest()

        assertEquals("My Dukan", request.name)
        assertEquals(setOf(categoryId), request.categoryIds)
        assertEquals("Baghdad", request.address)
        assertEquals(33.3, request.latitude)
        assertEquals(44.4, request.longitude)
        assertEquals(colorId, request.colorId)
        assertEquals("WIDE_IMAGE", request.style)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `List of DukanCategoryDto toCategoryList maps correctly`() {
        val id1 = Uuid.random()
        val id2 = Uuid.random()

        val dtos = listOf(
            DukanCategoryDto(id1, "Food", "food.png"),
            DukanCategoryDto(id2, "Clothes", "clothes.png")
        )

        val categories = dtos.toCategoryList()

        assertEquals(2, categories.size)
        assertEquals(id1, categories[0].id)
        assertEquals("Food", categories[0].name)
        assertEquals("food.png", categories[0].imageUrl)
        assertEquals(id2, categories[1].id)
        assertEquals("Clothes", categories[1].name)
        assertEquals("clothes.png", categories[1].imageUrl)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `List of DukanColorDto toColorsList maps correctly`() {
        val id1 = Uuid.random()
        val id2 = Uuid.random()
        val dtos = listOf(
            DukanColorDto(id1, "#FF0000"),
            DukanColorDto(id2, "#00FF00")
        )

        val colors = dtos.toColorsList()

        assertEquals(2, colors.size)
        assertEquals(id1, colors[0].id)
        assertEquals("#FF0000", colors[0].hexCode)
        assertEquals(id2, colors[1].id)
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

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `DukanDetailsDto toDukan maps isFavourite true correctly`() {
        val colorId = Uuid.random()
        val dukanId = Uuid.random()

        val dto = DukanDetailsDto(
            id = dukanId,
            name = "Favorite Dukan",
            imageUrl = "fav.png",
            isFavorite = true,
            address = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            color = DukanColorDto(colorId, "#FFFFFF"),
            style = "WIDE_IMAGE",
            ownerId = dukanId,
        )

        val dukan = dto.toDukan()
        assertTrue(dukan.isFavorite)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `Top dukan details are mapped correctly`() = runTest {
        val dto = TopDiscountedDukanDto(
            id = Uuid.random(),
            discount = 50.3,
            imageUrl = "top.png"
        )

        val dukanDiscountPreview = dto.toEntity()
        assertEquals(50, dukanDiscountPreview.discount)
        assertEquals("top.png", dukanDiscountPreview.imageUrl)
        assertEquals(dto.id, dukanDiscountPreview.id)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `Dukan activation status is mapped correctly`() = runTest {
       val dto = DukanActivationStatusResponse(
            status = "ACTIVATED",
        )

        val dukanActivationStatus = dto.toActivationStatus()
        assertEquals(Dukan.ActivationStatus.ACTIVATED.name, dukanActivationStatus.name)

    }
}