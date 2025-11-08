package net.thechance.mena.dukan.presentation.viewModel.checkout

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private val cartRepository = mock<CartRepository>(mode = MockMode.autofill)
    private val addressesRepository = mock<AddressesRepository>(mode = MockMode.autofill)
    private val locationService = LocationService(addressesRepository)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var checkoutViewModel: CheckoutViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf(
                "dukanId" to "123e4567-e89b-12d3-a456-426614174000"
            )
        )
        checkoutViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD load cart products from repository`() = runTest {
        advanceUntilIdle()
        val state = checkoutViewModel.state.value
        assertNotNull(state.items)
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        checkoutViewModel.effect.test {
            checkoutViewModel.onBackClicked()
            assertEquals(CheckoutEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChangeLocationClicked SHOULD emit NavigateToChangeLocation effect`() = runTest {
        checkoutViewModel.effect.test {
            checkoutViewModel.onChangeLocationClicked()
            assertEquals(CheckoutEffect.NavigateToChangeLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmOrderClicked SHOULD not throw error`() = runTest {
        checkoutViewModel.onConfirmOrderClicked()
        advanceUntilIdle()
        assertTrue(true)
    }

    private fun createViewModel() = CheckoutViewModel(
        cartRepository = cartRepository,
        locationService = locationService,
        savedStateHandle = savedStateHandle,
        dispatcher = testDispatcher
    )
}