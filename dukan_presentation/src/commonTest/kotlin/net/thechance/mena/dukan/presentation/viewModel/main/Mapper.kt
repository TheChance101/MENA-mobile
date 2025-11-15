@file: OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.main

import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.toBestNearestUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.toEditorPickUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.toUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UiStateMapperTest {

    @Test
    fun `MyDukanStatus toUiState maps correctly`() {
        val myStatus = MyDukanStatus(
            dukanName = "My Dukan",
            status = Dukan.Status.APPROVED
        )

        val uiState = myStatus.toUiState()

        assertEquals(
            MainScreenUiState.DukanState(
                name = "My Dukan",
                status = MainScreenUiState.DukanStatusUi.Approved
            ), uiState
        )
    }

    @Test
    fun `Dukan Status toUiState maps correctly`() {
        assertEquals(
            MainScreenUiState.DukanStatusUi.Pending,
            Dukan.Status.PENDING.toUiState()
        )
        assertEquals(
            MainScreenUiState.DukanStatusUi.Approved,
            Dukan.Status.APPROVED.toUiState()
        )
    }

    @Test
    fun `Dukan toBestNearestUiState maps correctly`() {
        val dukan = Dukan(
            id = Uuid.random(),
            name = "Editor Pick",
            imageUrl = "image_url",
            categories = emptySet(),
            coordinates = Dukan.Coordinates(
                latitude = 30.0,
                longitude = 31.0
            ),
            address = "Address",
            status = Dukan.Status.APPROVED,
            color = Color(
                hexCode = "#FFFFFF",
                id = Uuid.random()
            ),
            style = Dukan.Style.WIDE_IMAGE,
            isFavorite = false
        )

        val uiState = dukan.toBestNearestUiState()

        assertEquals(
            MainScreenUiState.BestNearestDukanUiState(
                id = dukan.id.toString(),
                name = dukan.name,
                imageUrl = dukan.imageUrl
            ), uiState
        )
    }

    @Test
    fun `Dukan toEditorPickUiState maps correctly`() {
        val dukan = Dukan(
            id = Uuid.random(),
            name = "Editor Pick",
            imageUrl = "image_url",
            categories = emptySet(),
            coordinates = Dukan.Coordinates(
                latitude = 30.0,
                longitude = 31.0
            ),
            address = "Address",
            status = Dukan.Status.APPROVED,
            color = Color(
                hexCode = "#FFFFFF",
                id = Uuid.random()
            ),
            style = Dukan.Style.WIDE_IMAGE,
            isFavorite = false
        )

        val uiState = dukan.toEditorPickUiState()

        assertEquals(
            MainScreenUiState.EditorPickDukanUiState(
                id = dukan.id.toString(),
                name = dukan.name,
                imageUrl = dukan.imageUrl,
                isFavorite = dukan.isFavorite
            ), uiState
        )
    }

    @Test
    fun `TopDiscountedDukanPreview toUiState maps correctly`() {
        val topDukan = TopDiscountedDukanPreview(
            id = Uuid.random(),
            imageUrl = "discount_image",
            discount = 25
        )

        val uiState = topDukan.toUiState()

        assertEquals(
            MainScreenUiState.DukanTopDiscount(
                id = topDukan.id,
                imageUrl = topDukan.imageUrl,
                discount = topDukan.discount
            ), uiState
        )
    }
}
