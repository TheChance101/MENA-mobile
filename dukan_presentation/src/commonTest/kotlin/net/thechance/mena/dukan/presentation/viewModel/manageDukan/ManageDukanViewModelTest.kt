package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.edit_shelf_successfully
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ManageDukanViewModelTest {

    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)
    private lateinit var manageDukanViewModel: ManageDukanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { shelfRepository.getMyDukanShelves() } returns dummyShelves
        everySuspend {
            productRepository.getProductsByShelfId(
                any(),
                any(),
                any()
            )
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalItems = 0,
            pageSize = 10,
            totalPages = 1
        )
        everySuspend {
            dukanManagementRepository.getDukanActivationStatus()
        } returns Dukan.ActivationStatus.ACTIVATED

        manageDukanViewModel = ManageDukanViewModel(
            shelfRepository,
            productRepository,
            dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD load shelves with correct count`() = runTest {
        advanceUntilIdle()
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `init SHOULD select exactly one shelf by default`() = runTest {
        advanceUntilIdle()
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.selectedShelf)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD select first shelf with correct id`() = runTest {
        advanceUntilIdle()
        manageDukanViewModel.state.test {
            val state = awaitItem()
            val expectedFirstShelfId = dummyShelves.first().id
            assertEquals(expectedFirstShelfId.toString(), state.selectedShelf?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf with correct name`() = runTest {
        advanceUntilIdle()
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals("Electronics", state.selectedShelf?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set shelvesState to EMPTY when repository returns empty list`() = runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } returns emptyList()

        manageDukanViewModel = ManageDukanViewModel(
            shelfRepository,
            productRepository,
            dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )

        advanceUntilIdle()

        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(ManageDukanUiState.ShelvesState.EMPTY, state.shelvesState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set shelvesState to EMPTY when repository throws exception`() = runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } throws DukanException("")

        manageDukanViewModel = ManageDukanViewModel(
            shelfRepository,
            productRepository,
            dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )

        advanceUntilIdle()

        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(ManageDukanUiState.ShelvesState.EMPTY, state.shelvesState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set selectedShelf to null when repository returns empty list`() = runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } returns emptyList()

        manageDukanViewModel = ManageDukanViewModel(
            shelfRepository,
            productRepository,
            dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )

        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertNull(state.selectedShelf)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackButtonClicked SHOULD emit NavigateBack effect`() = runTest {
        manageDukanViewModel.onBackClicked()

        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanUiEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddShelfClicked SHOULD emit NavigateToAddShelf effect`() = runTest {
        manageDukanViewModel.onAddShelfClicked()

        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanUiEffect.NavigateToAddShelf, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddProductClicked SHOULD emit NavigateToAddProduct effect`() = runTest {
        manageDukanViewModel.onAddProductClicked()

        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanUiEffect.NavigateToAddProduct, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onEditShelfClicked SHOULD emit NavigateToEditShelf effect`() = runTest {
        val firstShelf = dummyShelves.first()
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf.toUiState()) }

        manageDukanViewModel.onEditShelfClicked()

        manageDukanViewModel.effect.test {
            val expectedEffect = ManageDukanUiEffect.NavigateToManageShelf(
                shelfId = firstShelf.id.toString(),
                shelfTitle = firstShelf.name
            )
            assertEquals(expectedEffect, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductClick SHOULD emit NavigateToProductDetails effect`() = runTest {
        val product = fakeProducts().first().toUiState()

        manageDukanViewModel.onProductClicked(product)

        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanUiEffect.NavigateToProductDetails, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onEditProductClicked SHOULD emit NavigateToEditProduct effect with correct productId`() =
        runTest {
            val productId = fakeProducts().first().id.toString()

            manageDukanViewModel.onEditProductClicked(productId)

            manageDukanViewModel.effect.test {
                val expectedEffect = ManageDukanUiEffect.NavigateToEditProduct(
                    productId = productId
                )
                assertEquals(expectedEffect, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDismissSnackBar SHOULD hide snackbar`() = runTest {
        manageDukanViewModel.updateState { copy(snackBarState = snackBarSuccess) }

        manageDukanViewModel.onDismissSnackBar()

        val state = manageDukanViewModel.state.value
        assertNull(state.snackBarState)
    }

    @Test
    fun `onDismissSnackBar SHOULD reset snackbar state to null`() = runTest {
        manageDukanViewModel.updateState { copy(snackBarState = snackBarError) }

        manageDukanViewModel.onDismissSnackBar()

        val state = manageDukanViewModel.state.value
        assertNull(state.snackBarState)
    }

    @Test
    fun `isShelfSelected SHOULD return true when shelf is selected`() = runTest {
        val shelf = dummyShelvesUiState().first()
        manageDukanViewModel.updateState { copy(selectedShelf = shelf) }

        val isSelected = manageDukanViewModel.isShelfSelected(shelf)

        assertTrue(isSelected)
    }

    @Test
    fun `isShelfSelected SHOULD return false when shelf is not selected`() = runTest {
        val shelf = dummyShelvesUiState().first()
        val otherShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = shelf) }

        val isSelected = manageDukanViewModel.isShelfSelected(otherShelf)

        assertFalse(isSelected)
    }

    @Test
    fun `onShelfSelected SHOULD select shelf `() = runTest {
        val shelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = dummyShelvesUiState().first()) }

        manageDukanViewModel.onShelfSelected(shelf)

        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertEquals(shelf, selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD replace previously selected shelf with new one`() = runTest {
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        manageDukanViewModel.onShelfSelected(secondShelf)

        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertEquals(secondShelf, selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD not contain previous shelf when replacing`() = runTest {
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        manageDukanViewModel.onShelfSelected(secondShelf)

        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertTrue(selectedShelf != firstShelf)
    }

    @Test
    fun `onShelfSelected SHOULD have exactly one shelf when replacing`() = runTest {
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        manageDukanViewModel.onShelfSelected(secondShelf)

        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertNotNull(selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD do nothing when selecting the same shelf`() = runTest {
        val firstShelf = dummyShelvesUiState().first()
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        manageDukanViewModel.onShelfSelected(firstShelf)

        manageDukanViewModel.state.test {
            val initialState = awaitItem()
            assertEquals(firstShelf, initialState.selectedShelf)

            expectNoEvents()
        }
    }

    @Test
    fun `onShelfAdded SHOULD show snackbar`() = runTest {
        manageDukanViewModel.onShelfAdded(
            Res.string.add_shelf_successfully,
            SnackBarType.SUCCESS
        )

        val state = manageDukanViewModel.state.value
        assertNotNull(state.snackBarState)
    }

    @Test
    fun `onShelfAdded SHOULD show success snackbar type`() = runTest {
        manageDukanViewModel.onShelfAdded(
            Res.string.add_shelf_successfully,
            SnackBarType.SUCCESS
        )

        val state = manageDukanViewModel.state.value
        assertEquals(SnackBarType.SUCCESS, state.snackBarState?.snackBarType)
    }

    @Test
    fun `onShelfAdded SHOULD show correct success message`() = runTest {
        manageDukanViewModel.onShelfAdded(
            Res.string.add_shelf_successfully,
            SnackBarType.SUCCESS
        )

        val state = manageDukanViewModel.state.value
        assertEquals(Res.string.add_shelf_successfully, state.snackBarState?.message)
    }


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onShelfAdded SHOULD include new shelf in refreshed list`() = runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } returns newShelvesList

        manageDukanViewModel.onShelfAdded(
            Res.string.add_shelf_successfully,
            SnackBarType.SUCCESS
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = manageDukanViewModel.state.value
        assertTrue(state.shelves.any { it.id == newShelf.id.toString() })
    }


    @Test
    fun `onShelfAdded SHOULD clear products when no shelf selected`() = runTest {
        manageDukanViewModel.updateState { copy(selectedShelf = null) }

        manageDukanViewModel.onShelfAdded(
            Res.string.add_shelf_successfully,
            SnackBarType.SUCCESS
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = manageDukanViewModel.state.value
        assertTrue(state.products.asSnapshot().isEmpty())
    }

    @Test
    fun `onShelfAdded SHOULD reset product count to zero when no shelf selected`() =
        runTest {
            manageDukanViewModel.updateState { copy(selectedShelf = null) }

            manageDukanViewModel.onShelfAdded(
                Res.string.add_shelf_successfully,
                SnackBarType.SUCCESS
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val state = manageDukanViewModel.state.value
            assertEquals(0, state.totalProducts)
        }

    @Test
    fun `onEditShelfName SHOULD update shelf name`() = runTest {
        manageDukanViewModel.onEditShelfName(
            Res.string.edit_shelf_successfully,
            SnackBarType.SUCCESS
        )

        val state = manageDukanViewModel.state.value
        assertNotNull(state.snackBarState)
    }

    @Test
    fun `onShelfSelected SHOULD handle empty products gracefully`() = runTest {
        val shelf = dummyShelvesUiState().first()
        everySuspend {
            productRepository.getProductsByShelfId(
                shelf.id,
                any(),
                any()
            )
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalItems = 0,
            pageSize = 10,
            totalPages = 1
        )

        manageDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = manageDukanViewModel.state.value
        assertTrue(state.products.asSnapshot().isEmpty())
    }

    @Test
    fun `onDismissDeleteShelfConfirmationDialog hides the dialog`() = runTest {
        manageDukanViewModel.updateState { copy(deleteDialog = expectedDeleteDialogState) }

        manageDukanViewModel.onDismissDeleteShelfConfirmationDialog()

        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertNull(state.deleteDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfDialog show the dialog`() = runTest {
        manageDukanViewModel.onShowDeleteShelfDialog(
            shelfId = testShelfId
        )
        advanceUntilIdle()
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.deleteDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfDialog displays dialog with delete type when no products`() =
        runTest {
            manageDukanViewModel.updateState {
                copy(
                    products = emptyFlow()
                )
            }

            manageDukanViewModel.onShowDeleteShelfDialog(
                shelfId = testShelfId
            )
            advanceUntilIdle()

            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(
                    expectedDeleteDialogState,
                    state.deleteDialog
                )
            }
        }

    @Test
    fun `onShowDeleteShelfDialog displays dialog with DISMISS type when products exist`() =
        runTest {
            manageDukanViewModel.updateState {
                copy(
                    totalProducts = 1
                )
            }

            manageDukanViewModel.onShowDeleteShelfDialog(shelfId = testShelfId)
            advanceUntilIdle()

            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(ManageDukanUiState.DialogType.DISMISS, state.deleteDialog?.type)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `deleteShelf successfully should dismiss dialog`() = runTest {
        everySuspend { shelfRepository.deleteShelf(testShelfId) } returns Unit

        manageDukanViewModel.onDeleteConfirmed(testShelfId)
        advanceUntilIdle()

        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertNull(state.deleteDialog)
        }
    }

    @Test
    fun `deleteShelf successfully should show snackBar with delete shelf successfully`() =
        runTest {
            everySuspend { shelfRepository.deleteShelf(testShelfId) } returns Unit

            manageDukanViewModel.onDeleteConfirmed(testShelfId)
            advanceUntilIdle()

            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(snackBarDeleteSuccess, state.snackBarState)
            }
        }

    @Test
    fun `deleteShelf throw exception should dismiss dialog`() =
        runTest {
            everySuspend { shelfRepository.deleteShelf(testShelfId) } throws DukanException("")

            manageDukanViewModel.onDeleteConfirmed(testShelfId)
            advanceUntilIdle()

            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertNull(state.deleteDialog)
            }
        }

    @Test
    fun `deleteShelf throw exception should show snackBar with error deleting shelf`() =
        runTest {
            everySuspend { shelfRepository.deleteShelf(testShelfId) } throws DukanException("")

            manageDukanViewModel.onDeleteConfirmed(testShelfId)
            advanceUntilIdle()
            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(snackBarDeleteError, state.snackBarState)
            }
        }
}

private const val testShelfId = "1"

private val snackBarSuccess = SnackBarUiState(
    SnackBarType.SUCCESS,
    Res.string.add_shelf_successfully
)

private val snackBarError = SnackBarUiState(
    SnackBarType.ERROR,
    Res.string.shelf_name_is_already_exist
)

private val snackBarDeleteSuccess = SnackBarUiState(
    snackBarType = SnackBarType.SUCCESS,
    message = Res.string.delete_shelf_success
)

private val snackBarDeleteError = SnackBarUiState(
    snackBarType = SnackBarType.ERROR,
    message = Res.string.error_general
)

private val expectedDeleteDialogState =
    ManageDukanUiState.DeleteDialogState(
        title = Res.string.delete_shelf_title,
        description = Res.string.delete_shelf_description,
        type = ManageDukanUiState.DialogType.DELETE,
        shelfId = testShelfId,
    )

private fun dummyShelvesUiState(): List<ManageDukanUiState.ShelfUiState> {
    return listOf(
        ManageDukanUiState.ShelfUiState(
            id = "shelf_1",
            name = "Electronics"
        ),
        ManageDukanUiState.ShelfUiState(
            id = "shelf_2",
            name = "Clothing"
        ),
        ManageDukanUiState.ShelfUiState(
            id = "shelf_3",
            name = "Books"
        )
    )
}

@OptIn(ExperimentalUuidApi::class)
private val dummyShelves = listOf(
    Shelf(
        id = Uuid.parse("8a0f5a2c-3c5b-4e2e-b7a1-9e54ac62f391"),
        name = "Electronics"
    ),
    Shelf(
        id = Uuid.parse("1c93b72a-9a1e-4c85-8e17-2a785df9c44d"),
        name = "Clothing"
    ),
    Shelf(
        id = Uuid.parse("6fe75d89-f8f8-4993-a277-52c0706fb666"),
        name = "Books"
    )
)

@OptIn(ExperimentalUuidApi::class)
private val newShelf = Shelf(Uuid.parse("9f4a1b9c-8f1a-4f9b-9e4a-1b9c8f1a4f9b"), "New Shelf")

@OptIn(ExperimentalUuidApi::class)
private val newShelvesList = dummyShelves + newShelf


@OptIn(ExperimentalUuidApi::class)
private fun fakeProducts(): List<Product> {
    return listOf(
        Product(
            id = Uuid.random(),
            name = "iPhone 15",
            description = "Latest iPhone model",
            price = Price(
                base = 999.99,
                final = 999.99
            ),
            createdAt = "2023-08-01T10:00:00Z",
            imageUrls = listOf("https://example.com/iphone.jpg"),
            quantityInCart = 10,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000123"),
            isFavorite = false
        ),
        Product(
            id = Uuid.random(),
            name = "MacBook Pro",
            description = "Professional laptop",
            price = Price(
                base = 1999.99,
                final = 1999.99
            ),
            imageUrls = listOf("https://example.com/macbook.jpg"),
            createdAt = "2023-08-01T10:00:00Z",
            quantityInCart = 10,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        ),
        Product(
            id = Uuid.random(),
            name = "T-Shirt",
            description = "Cotton t-shirt",
            price = Price(
                base = 29.99,
                final = 29.99
            ),
            createdAt = "2023-08-01T10:00:00Z",
            imageUrls = listOf("https://example.com/tshirt.jpg"),
            quantityInCart = 20,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000125"),
            isFavorite = false
        )
    )
}