package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
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
import net.thechance.mena.dukan.domain.entity.DukanPreview
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DukansViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
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
            dukanRepository.getDukansByCategory(
                categoryId = any(),
                page = any(),
                size = any()
            )
        } returns PagedResult(
            items = dummyDukanPreviews,
            currentPage = 1,
            totalPages = 1,
            totalItems = dummyDukanPreviews.size.toLong()
        )

        dukansViewModel = CategoryDukansViewModel(
            dukanRepository = dukanRepository,
            savedStateHandle = savedStateHandle,
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
        val pager = dukansViewModel.initializedPager
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
    fun `init SHOULD initialize pager with correct category`() = runTest {
        // When
        val pager = dukansViewModel.initializedPager

        // Then
        assertNotNull(pager)
    }

    @Test
    fun `init SHOULD load dukans with correct count`() = runTest {
        // When
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle()

        dukansViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.dukans.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set dukans state to LOADED when dukans are available`() = runTest {
        // When
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle()

        dukansViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(CategoryDukansUiState.DukansState.LOADED, state.dukansState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick SHOULD emit NavigateBack effect`() = runTest {
        // When
        dukansViewModel.onBackClick()

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
        dukansViewModel.onDukanClick(dukan)

        // Then
        dukansViewModel.effect.test {
            assertEquals(CategoryDukansEffects.NavigateToDukanDetails(dukan.id), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClick SHOULD toggle favorite status successfully`() = runTest {
        // Given
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle()
        assertFalse(dummyDukansUiState().first().isFavorite)

        // When
        dukansViewModel.onFavoriteClick(dummyDukansUiState().first())
        advanceUntilIdle()

        // Then
        val state = dukansViewModel.state.value
        val updatedDukan = state.dukans.items.find { it.id == dummyDukansUiState().first().id }
        assertNotNull(updatedDukan)
        assertTrue(updatedDukan.isFavorite)
    }

    @Test
    fun `onFavoriteClick SHOULD toggle favorite from false to true`() = runTest {
        // Given
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle()
        val dukan = dummyDukansUiState().first()
        assertFalse(dukan.isFavorite)

        // When
        dukansViewModel.onFavoriteClick(dukan)
        advanceUntilIdle()

        // Then
        val state = dukansViewModel.state.value
        val updatedDukan = state.dukans.items.find { it.id == dukan.id }
        assertNotNull(updatedDukan)
        assertTrue(updatedDukan.isFavorite)
    }

    @Test
    fun `onFavoriteClick SHOULD toggle favorite from true to false`() = runTest {
        // Given
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle()

        dukansViewModel.onFavoriteClick(dummyDukansUiState().first())
        advanceUntilIdle()

        val favoriteDukan =
            dukansViewModel.state.value.dukans.items.find { it.id == dummyDukansUiState().first().id }!!
        assertTrue(favoriteDukan.isFavorite)

        // When
        dukansViewModel.onFavoriteClick(favoriteDukan)
        advanceUntilIdle()

        // Then
        val updatedDukan =
            dukansViewModel.state.value.dukans.items.find { it.id == dummyDukansUiState().first().id }!!
        assertFalse(updatedDukan.isFavorite)
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
            dukanRepository = dukanRepository,
            savedStateHandle = emptySavedStateHandle,
            defaultDispatcher = testDispatcher
        )

        // When
        val pager = emptyViewModel.initializedPager
        advanceUntilIdle()

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
            dukanRepository = dukanRepository,
            savedStateHandle = nullSavedStateHandle,
            defaultDispatcher = testDispatcher
        )

        // When 
        val pager = nullViewModel.initializedPager
        advanceUntilIdle()

        nullViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("", state.categoryId)
            assertEquals("", state.categoryTitle)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

// ===== FAKE DATA FUNCTIONS =====

private fun dummyDukansUiState(): List<CategoryDukansUiState.DukanUiState> {
    return listOf(
        CategoryDukansUiState.DukanUiState(
            id = "dukan1",
            name = "Electronics Store",
            imageUrl = "https://example.com/electronics.jpg",
            isFavorite = false
        ),
        CategoryDukansUiState.DukanUiState(
            id = "dukan2",
            name = "Tech Hub",
            imageUrl = "https://example.com/tech.jpg",
            isFavorite = true
        ),
        CategoryDukansUiState.DukanUiState(
            id = "dukan3",
            name = "Gadget World",
            imageUrl = "https://example.com/gadget.jpg",
            isFavorite = false
        )
    )
}

private val dummyDukanPreviews = listOf(
    DukanPreview(
        id = "dukan1",
        name = "Electronics Store",
        imageUrl = "https://example.com/electronics.jpg"
    ),
    DukanPreview(
        id = "dukan2",
        name = "Tech Hub",
        imageUrl = "https://example.com/tech.jpg"
    ),
    DukanPreview(
        id = "dukan3",
        name = "Gadget World",
        imageUrl = "https://example.com/gadget.jpg"
    )
)
