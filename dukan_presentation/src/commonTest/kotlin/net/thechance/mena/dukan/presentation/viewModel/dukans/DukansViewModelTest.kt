package net.thechance.mena.dukan.presentation.viewModel.dukans

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
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
import net.thechance.mena.dukan.domain.exceptions.DukanException
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
    private lateinit var dukansViewModel: DukansViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        savedStateHandle = SavedStateHandle(mapOf(
            "categoryId" to "cat1",
            "categoryTitle" to "Electronics"
        ))

        everySuspend {
            dukanRepository.getDukansByCategory(
                categoryId = any(),
                page = any(),
                size = any()
            )
        } returns PagedResult(
            items = dummyDukans,
            currentPage = 1,
            totalPages = 1,
            totalItems = dummyDukans.size.toLong()
        )

        dukansViewModel = DukansViewModel(
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
        // When - Trigger initialization
        val pager = dukansViewModel.initializedPager
        advanceUntilIdle() // Wait for all coroutines to complete
        
        dukansViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(DukansState.LOADED, state.dukansState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick SHOULD emit NavigateBack effect`() = runTest {
        // When
        dukansViewModel.onBackClick()

        // Then
        dukansViewModel.effect.test {
            assertEquals(DukansEffects.NavigateBack, awaitItem())
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
            assertEquals(DukansEffects.NavigateToDukanDetails(dukan.id), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClick SHOULD toggle favorite status successfully`() = runTest {
        // Given
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
    fun `onFavoriteClick SHOULD handle favorite toggle error gracefully`() = runTest {
        // Given
        val errorSavedStateHandle = SavedStateHandle(mapOf(
            "categoryId" to "cat1",
            "categoryTitle" to "Electronics"
        ))
        
        everySuspend {
            dukanRepository.getDukansByCategory(
                categoryId = any(),
                page = any(),
                size = any()
            )
        } throws DukanException("Network error")
        
        val errorViewModel = DukansViewModel(
            dukanRepository = dukanRepository,
            savedStateHandle = errorSavedStateHandle,
            defaultDispatcher = testDispatcher
        )
        advanceUntilIdle()
        val dukan = dummyDukansUiState().first()

        // When
        errorViewModel.onFavoriteClick(dukan)
        advanceUntilIdle()

        // Then
        val state = errorViewModel.state.value
        val updatedDukan = state.dukans.items.find { it.id == dukan.id }
        assertNotNull(updatedDukan)
        // Should remain unchanged due to error
        assertEquals(dukan.isFavorite, updatedDukan.isFavorite)
    }


    @Test
    fun `init SHOULD handle empty category gracefully`() = runTest {
        // Given
        val emptySavedStateHandle = SavedStateHandle(mapOf(
            "categoryId" to "",
            "categoryTitle" to ""
        ))
        
        val emptyViewModel = DukansViewModel(
            dukanRepository = dukanRepository,
            savedStateHandle = emptySavedStateHandle,
            defaultDispatcher = testDispatcher
        )

        // When
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
        
        val nullViewModel = DukansViewModel(
            dukanRepository = dukanRepository,
            savedStateHandle = nullSavedStateHandle,
            defaultDispatcher = testDispatcher
        )

        // When 
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

private fun dummyDukansUiState(): List<DukanUiState> {
    return listOf(
        DukanUiState(
            id = "dukan1",
            name = "Electronics Store",
            imageUrl = "https://example.com/electronics.jpg",
            isFavorite = false
        ),
        DukanUiState(
            id = "dukan2",
            name = "Tech Hub",
            imageUrl = "https://example.com/tech.jpg",
            isFavorite = true
        ),
        DukanUiState(
            id = "dukan3",
            name = "Gadget World",
            imageUrl = "https://example.com/gadget.jpg",
            isFavorite = false
        )
    )
}

private val dummyDukans = listOf(
    Dukan(
        id = "dukan1",
        name = "Electronics Store",
        imageUrl = "https://example.com/electronics.jpg",
        categories = emptySet(),
        coordinates = Dukan.Coordinates(0.0, 0.0),
        address = "Test Address",
        status = Dukan.Status.APPROVED,
        color = Color("blue", "#0000FF"),
        style = Dukan.Style.WIDE_IMAGE
    ),
    Dukan(
        id = "dukan2",
        name = "Tech Hub",
        imageUrl = "https://example.com/tech.jpg",
        categories = emptySet(),
        coordinates = Dukan.Coordinates(0.0, 0.0),
        address = "Test Address",
        status = Dukan.Status.APPROVED,
        color = Color("green", "#00FF00"),
        style = Dukan.Style.SMALL_IMAGE
    ),
    Dukan(
        id = "dukan3",
        name = "Gadget World",
        imageUrl = "https://example.com/gadget.jpg",
        categories = emptySet(),
        coordinates = Dukan.Coordinates(0.0, 0.0),
        address = "Test Address",
        status = Dukan.Status.APPROVED,
        color = Color("red", "#FF0000"),
        style = Dukan.Style.NO_IMAGE
    )
)
