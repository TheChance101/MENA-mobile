package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DukansViewModelTest {

    private val dukanDiscoveryRepository = mock<DukanDiscoveryRepository>(mode = MockMode.autofill)
    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dukansViewModel: CategoryDukansViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(
            mapOf(
                "categoryId" to "cat1",
                "categoryTitle" to "Electronics"
            )
        )

        everySuspend {
            dukanDiscoveryRepository.getDukansByCategory(
                categoryId = any(),
                page = any(),
                size = any()
            )
        } returns PagedResult(
            items = dummyDukanPreviews,
            currentPage = 1,
            totalPages = 1,
            totalItems = dummyDukanPreviews.size.toLong(),
            pageSize = 10
        )

        dukansViewModel = CategoryDukansViewModel(
            dukanDiscoveryRepository = dukanDiscoveryRepository,
            savedStateHandle = savedStateHandle,
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD load category info from SavedStateHandle`() = runTest {
        // When
        advanceUntilIdle()

        dukansViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("cat1", state.categoryId)
            assertEquals("Electronics", state.categoryTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `init SHOULD load dukans with correct count`() = runTest {
        advanceUntilIdle()
        // When
        dukansViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.dukans.asSnapshot().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick SHOULD emit NavigateBack effect`() = runTest {
        // When
        dukansViewModel.onBackClicked()

        // Then
        dukansViewModel.effect.test {
            assertEquals(CategoryDukansEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDukanClick SHOULD emit NavigateToDukanDetails effect with correct dukan id`() = runTest {
        // Given
        val dukan = dummyDukansUiState().first()

        // When
        dukansViewModel.onDukanClicked(dukan)

        // Then
        dukansViewModel.effect.test {
            assertEquals(CategoryDukansEffects.NavigateToDukanDetails(dukan.id), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD handle empty category gracefully`() = runTest {
        // Given
        val emptySavedStateHandle = SavedStateHandle(
            mapOf(
                "categoryId" to "",
                "categoryTitle" to ""
            )
        )

        val emptyViewModel = CategoryDukansViewModel(
            dukanDiscoveryRepository = dukanDiscoveryRepository,
            savedStateHandle = emptySavedStateHandle,
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )

        // When
        emptyViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("", state.categoryId)
            assertEquals("", state.categoryTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD handle null category gracefully`() = runTest {
        // Given
        val nullSavedStateHandle = SavedStateHandle(emptyMap())

        val nullViewModel = CategoryDukansViewModel(
            dukanDiscoveryRepository = dukanDiscoveryRepository,
            savedStateHandle = nullSavedStateHandle,
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )

        // When 
        nullViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("", state.categoryId)
            assertEquals("", state.categoryTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onFavoriteDukanClicked SHOULD toggle isFavorite for correct dukan`() = runTest {
        // Given
        val targetDukan = dummyDukanPreviews[0]
        everySuspend {
            dukanManagementRepository.updateFavoriteDukanStatus(
                targetDukan.id.toString()
            )
        }

        advanceUntilIdle()

        // When
        dukansViewModel.onFavoriteDukanClicked(targetDukan.id.toString())

        // Wait for toggle to complete
        advanceUntilIdle()

        // Then
        val updatedDukans = dukansViewModel.state.value.dukans.asSnapshot()
        val updatedTarget = updatedDukans.first { it.id == targetDukan.id.toString() }

        assertEquals(!targetDukan.isFavorite, updatedTarget.isFavorite)
    }

}

// ===== FAKE DATA FUNCTIONS =====

private fun dummyDukansUiState(): List<CategoryDukansUiState.DukanUiState> {
    return listOf(
        CategoryDukansUiState.DukanUiState(
            id = "123e4567-e89b-12d3-a456-426614174001",
            name = "Electronics Store",
            imageUrl = "https://example.com/electronics.jpg",
            isFavorite = false
        ),
        CategoryDukansUiState.DukanUiState(
            id = "123e4567-e89b-12d3-a456-426614174002",
            name = "Tech Hub",
            imageUrl = "https://example.com/tech.jpg",
            isFavorite = true
        ),
        CategoryDukansUiState.DukanUiState(
            id = "123e4567-e89b-12d3-a456-426614174003",
            name = "Gadget World",
            imageUrl = "https://example.com/gadget.jpg",
            isFavorite = false
        )
    )
}

@OptIn(ExperimentalUuidApi::class)
private val dummyDukanPreviews = listOf(
    Dukan(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174001"),
        name = "Electronics Store",
        imageUrl = "https://example.com/electronics.jpg",
        categories = emptySet(),
        isFavorite = true,
        coordinates = Dukan.Coordinates(
            latitude = 12.34,
            longitude = 56.78
        ),
        address = "",
        status = Dukan.Status.PENDING,
        color = Color(
            id = Uuid.random(),
            hexCode = ""
        ),
        style = Dukan.Style.WIDE_IMAGE
    ),
    Dukan(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174002"),
        name = "Tech Hub",
        imageUrl = "https://example.com/tech.jpg",
        categories = emptySet(),
        isFavorite = false,
        coordinates = Dukan.Coordinates(
            latitude = 12.34,
            longitude = 56.78
        ),
        address = "",
        status = Dukan.Status.PENDING,
        color = Color(
            id = Uuid.random(),
            hexCode = ""
        ),
        style = Dukan.Style.WIDE_IMAGE
    ),
    Dukan(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
        name = "Gadget World",
        imageUrl = "https://example.com/gadget.jpg",
        categories = emptySet(),
        isFavorite = false,
        coordinates = Dukan.Coordinates(
            latitude = 12.34,
            longitude = 56.78
        ),
        address = "",
        status = Dukan.Status.PENDING,
        color = Color(
            id = Uuid.random(),
            hexCode = ""
        ),
        style = Dukan.Style.WIDE_IMAGE
    )
)
