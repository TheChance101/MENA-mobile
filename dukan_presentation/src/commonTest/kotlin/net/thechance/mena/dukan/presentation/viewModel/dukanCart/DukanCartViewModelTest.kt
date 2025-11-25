package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class DukanCartViewModelTest {

    private val cartRepository = mock<CartRepository>(mode = MockMode.autofill)
    private val dukanRepository = mock<DukanManagementRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DukanCartViewModel

    private fun createViewModel() = DukanCartViewModel(
        cartRepository = cartRepository,
        dukanRepository = dukanRepository,
        savedStateHandle = savedStateHandle,
        defaultDispatcher = testDispatcher
    )

    private fun dummyCart() = Cart(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
        totalPriceBeforeDiscount = 500.0,
        discount = 0.0,
        totalPriceAfterDiscount = 500.0,
    )

    private fun dummyDukan() = Dukan(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174001"),
        name = "Test Dukan",
        address = "123 Street",
        imageUrl = "https://image.com",
        coordinates = Dukan.Coordinates(30.0, 31.0),
        color = Color(
            id = Uuid.random(),
            hexCode = "#FF0000"
        ),
        style = Dukan.Style.WIDE_IMAGE,
        categories = emptySet(),
        status = Dukan.Status.APPROVED,
        isFavorite = false
    )

    private val dummyProduct1 = Product(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174002"),
        name = "Phone",
        description = "Smart phone",
        price = Price(
            base = 200.0,
            final = 200.0
        ),
        imageUrls = listOf("phone.png"),
        createdAt = "2025-10-10T12:00:00Z",
        quantityInCart = 2,
        shelfId = null,
        isFavorite = false
    )

    private fun dummyPagedProducts() = PagedResult(
        items = listOf(dummyProduct1),
        currentPage = 1,
        totalItems = 1L,
        pageSize = 10,
        totalPages = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("dukanId" to dummyDukan().id.toString()))

        everySuspend { cartRepository.getCartInfo(dummyDukan().id.toString()) } returns dummyCart()
        everySuspend { dukanRepository.getDukanDetailsByDukanId(dummyDukan().id.toString()) } returns dummyDukan()
        everySuspend {
            cartRepository.getCartProducts(Uuid.parse(dummyDukan().id.toString()), any(), any())
        } returns dummyPagedProducts()

        viewModel = createViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init SHOULD set cartState to ERROR NO_INTERNET when repository fails`() = runTest {
        everySuspend { cartRepository.getCartInfo(any()) } throws NoInternetException("No Internet")

        val errorVm = createViewModel()
        advanceUntilIdle()

        val state = errorVm.state.value
        assertEquals(DukanCartUiState.CartState.NO_INTERNET, state.cartState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init SHOULD set cartState to ERROR NoSuchItemException when repository fails`() = runTest {
        everySuspend { cartRepository.getCartInfo(any()) } throws NoSuchItemException("Item Not Found")

        val errorVm = createViewModel()
        advanceUntilIdle()

        val state = errorVm.state.value
        assertEquals(0.0, state.totalPrice)
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            assertEquals(DukanCartEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load cart info successfully`() = runTest {
        everySuspend { cartRepository.getCartInfo(any()) } returns dummyCart()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(500.0, state.totalPrice)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load dukan info successfully`() = runTest {
        everySuspend { dukanRepository.getDukanDetailsByDukanId(any()) } returns dummyDukan()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(dummyDukan().name, state.dukanInfo.name)
            assertEquals(dummyDukan().imageUrl, state.dukanInfo.imageUrl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onIncreaseItemQuantityClicked SHOULD increase product quantity in cart `() = runTest {
        val productId = "1"
        val quantity = 20
        everySuspend { cartRepository.addProductQuantity(any()) } returns Unit

        viewModel.onIncreaseItemQuantityClicked(productId, newQuantity = quantity)

        advanceUntilIdle()
        verifySuspend {
            cartRepository.updateProductQuantity(any())
        }
    }

    @Test
    fun `onDecreaseItemQuantityClicked SHOULD decrease product quantity in cart `() = runTest {
        val productId = "1"
        val quantity = 10

        everySuspend { cartRepository.updateProductQuantity(any()) } returns Unit

        viewModel.onDecreaseItemQuantityClicked(productId, newQuantity = quantity)

        advanceUntilIdle()
        verifySuspend { cartRepository.updateProductQuantity(any()) }
    }

    @Test
    fun `onDecreaseItemQuantityClicked SHOULD NOT update when quantity is zero`() = runTest {
        advanceUntilIdle()

        val productId = dummyProduct1.id.toString()
        val newQuantity = 0

        viewModel.onDecreaseItemQuantityClicked(productId, newQuantity)
        advanceUntilIdle()
    }

    @Test
    fun `onRemoveItemClicked SHOULD remove product quantity in cart `() = runTest {
        val productId = "1"

        everySuspend { cartRepository.deleteProductFromCart(any(), productId) } returns Unit

        viewModel.onRemoveItemClicked(productId)

        advanceUntilIdle()
        verifySuspend { cartRepository.deleteProductFromCart(any(), productId) }
    }


    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {

        viewModel.onDismissSnackBar()

        assertTrue(viewModel.state.value.snackBarState == null)
    }

    @Test
    fun `onErrorUpdateProductQuantity SHOULD show error snackbar when NoInternetException thrown`() =
        runTest {
            val productId = "1"
            val quantity = 5

            everySuspend { cartRepository.updateProductQuantity(any()) } throws NoInternetException()

            viewModel.onIncreaseItemQuantityClicked(productId, newQuantity = quantity)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(Res.string.no_internet_connection, state.snackBarState?.message)
            assertEquals(SnackBarType.ERROR, state.snackBarState?.snackBarType)
        }


}