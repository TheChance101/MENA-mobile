package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

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
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.DUKAN_ID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dukanDetailsViewModel: DukanDetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(DUKAN_ID to "dukan_123"))

        everySuspend { dukanRepository.getDukanDetailsByDukanId(any()) } returns dummyDukanDetails()
        everySuspend {
            shelfRepository.getShelvesByDukanId(any(), any(), any())
        } returns PagedResult(
            items = dummyShelves(),
            currentPage = 1,
            totalItems = 3L,
            totalPages = 1
        )
        everySuspend {
            productRepository.getProductsByShelfId(any(), any(), any())
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalItems = 0L,
            totalPages = 1
        )

        dukanDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD set isDukanInfoLoading to false after successful load`() = runTest {
        // When
        advanceUntilIdle()
        val state = dukanDetailsViewModel.state.value
        // Then
        assertFalse(state.isDukanInfoLoading)
    }


    @Test
    fun `init SHOULD set isDukanInfoLoading to false when details loading fails`() = runTest {
        // Given
        everySuspend { dukanRepository.getDukanDetailsByDukanId(any()) } throws Exception("Network Error")

        // When
        val errorViewModel = createViewModel()
        advanceUntilIdle()
        val state = errorViewModel.state.value

        // Then
        assertFalse(state.isDukanInfoLoading)
    }

    @Test
    fun `onShelfClicked SHOULD update selected shelf id in state`() = runTest {
        // Given
        advanceUntilIdle()
        val newShelfId = "shelf_2"

        // When
        dukanDetailsViewModel.onShelfClicked(newShelfId)
        val state = dukanDetailsViewModel.state.value

        // Then
        assertEquals(newShelfId, state.shelfIdSelected)
    }

    @Test
    fun `onShelfClicked SHOULD load correct number of products`() = runTest {
        // Given
        val targetShelfId = "shelf_1"
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        val pager = dukanDetailsViewModel.pagerProduct
        pager.load()
        advanceUntilIdle()

        // Then
        pager.flow.test {
            assertEquals(1, awaitItem().items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onShelfClicked SHOULD load product with correct ID`() = runTest {
        // Given
        val targetShelfId = "shelf_1"
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        val pager = dukanDetailsViewModel.pagerProduct
        pager.load()
        advanceUntilIdle()

        // Then
        pager.flow.test {
            assertEquals("product_1", awaitItem().items.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onShelfClicked SHOULD load product with correct name`() = runTest {
        // Given
        val targetShelfId = "shelf_1"
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        val pager = dukanDetailsViewModel.pagerProduct
        pager.load()
        advanceUntilIdle()

        // Then
        pager.flow.test {
            assertEquals("Laptop", awaitItem().items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getProductsPager SHOULD return the same cached pager instance for the same shelfId`() =
        runTest {
            // When
            val pagerInstance1 = dukanDetailsViewModel.onShelfClicked("shelf_1")
            val pagerInstance2 = dukanDetailsViewModel.onShelfClicked("shelf_1")

            // Then
            assertTrue(pagerInstance1 === pagerInstance2)
        }

    @Test
    fun `when onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        // Then
        dukanDetailsViewModel.effect.test {
            // When
            dukanDetailsViewModel.onBackClicked()
            assertEquals(DukanDetailsEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when onViewAllShelfProductsClicked SHOULD emit correct navigation effect`() = runTest {
        // Given
        val shelfId = "id123"
        val shelfName = "shelf"

        // Then
        dukanDetailsViewModel.effect.test {
            // When
            dukanDetailsViewModel.onViewAllShelfProductsClicked(shelfId, shelfName)
            assertEquals(
                DukanDetailsEffects.NavigateToViewAllShelfProducts(shelfId, shelfName),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when onViewDukanOnMapClicked SHOULD emit correct navigation effect`() = runTest {
        // Given
        val lat = 28.0
        val lon = 29.0

        // Then
        dukanDetailsViewModel.effect.test {
            // When
            dukanDetailsViewModel.onViewDukanOnMapClicked(lat, lon)
            assertEquals(DukanDetailsEffects.NavigateToViewDukanOnMap(lat, lon), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    private fun createViewModel() = DukanDetailsViewModel(
        dukanRepository = dukanRepository,
        shelfRepository = shelfRepository,
        productRepository = productRepository,
        defaultDispatcher = testDispatcher,
        savedStateHandle = savedStateHandle
    )

    private fun setupProductsForShelf(shelfId: String) {
        everySuspend {
            productRepository.getProductsByShelfId(shelfId = shelfId, page = any(), size = any())
        } returns PagedResult(
            items = fakeProducts(),
            currentPage = 1,
            totalItems = 1L,
            totalPages = 1
        )
    }
}

private fun dummyDukanDetails() = Dukan(
    id = "dukan_123",
    name = "Test Dukan",
    address = "123 Test Street",
    imageUrl = "https://example.com/image.png",
    coordinates = Dukan.Coordinates(latitude = 30.0, longitude = 31.0),
    color = Color(id = "color_1", hexCode = "#FF0000"),
    style = Dukan.Style.WIDE_IMAGE,
    categories = emptySet(),
    status = Dukan.Status.APPROVED,
)

private fun dummyShelves() = listOf(
    Shelf(id = "shelf_1", name = "Electronics"),
    Shelf(id = "shelf_2", name = "Clothing"),
    Shelf(id = "shelf_3", name = "Books")
)

private fun fakeProducts(): List<Product> = listOf(
    Product(
        id = "product_1",
        name = "Laptop",
        description = "A cool laptop",
        price = 1200.0,
        imageUrls = emptyList(),
        createdAt = "2025-10-10T12:00:00Z"
    )
)