package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.mapper.toEntity
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.CoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class AddressesScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val addressRepository: AddressesRepository = mockk(relaxed = true)
    private lateinit var viewModel: AddressesScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddressesScreenViewModel(addressRepository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init() should fetch addresses and update state on success`() = runTest {
        val fakeAddresses = listOf(createFakeAddress())
        coEvery { addressRepository.getUserAddresses() } returns fakeAddresses.map { it.toEntity() }
        coEvery { addressRepository.getActiveAddress() } returns null

        viewModel = AddressesScreenViewModel(addressRepository, testDispatcher)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.addresses.size)
        assertEquals(fakeAddresses.first().id, viewModel.state.value.addresses.first().id)
    }

    @Test
    fun `onBackButtonClicked() should emit NavigateBack effect`() = runTest {
        coEvery { addressRepository.getUserAddresses() } returns emptyList()
        coEvery { addressRepository.getActiveAddress() } returns null
        viewModel.effect.test {
            viewModel.onBackButtonClicked()
            assertTrue(awaitItem() is AddressesScreenUIEffect.NavigateBack)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onAddButtonClicked() should emit NavigateToAddressDetailsScreen with null id`() =
        runTest {
            coEvery { addressRepository.getUserAddresses() } returns emptyList()
            coEvery { addressRepository.getActiveAddress() } returns null

            viewModel.effect.test {
                viewModel.onAddButtonClicked()
                val effect = awaitItem() as AddressesScreenUIEffect.NavigateToAddressDetailsScreen
                assertEquals(null, effect.addressUIState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `onDeleteAddressClicked() should show delete dialog`() = runTest {
        coEvery { addressRepository.getUserAddresses() } returns emptyList()
        coEvery { addressRepository.getActiveAddress() } returns null
        val addressId = Uuid.random()

        viewModel.onDeleteAddressClicked(addressId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.deleteDialogUIState.isVisible)
        assertEquals(addressId, viewModel.state.value.deleteDialogUIState.addressId)
    }
    @Test
    fun `onConfirmDeleteAddress() should call deleteAddress when address is valid`() = runTest {
        val address = createFakeAddress().copy(isMainAddress = false)
        coEvery { addressRepository.getUserAddresses() } returns listOf(address.toEntity())
        coEvery { addressRepository.getActiveAddress() } returns null
        coEvery { addressRepository.deleteAddress(any()) } returns Unit

        viewModel = AddressesScreenViewModel(addressRepository, testDispatcher)
        advanceUntilIdle()
        viewModel.onDeleteAddressClicked(address.id!!)
        advanceUntilIdle()

        viewModel.onConfirmDeleteAddress()
        advanceUntilIdle()

        coVerify(exactly = 1) { addressRepository.deleteAddress(address.id) }
    }

    @Test
    fun `onConfirmDeleteAddress() should delete address and show success snackbar`() = runTest {
        val address = createFakeAddress().copy(isMainAddress = false)
        coEvery { addressRepository.getUserAddresses() } returns listOf(address.toEntity())
        coEvery { addressRepository.getActiveAddress() } returns null
        coEvery { addressRepository.deleteAddress(any()) } returns Unit
        
        viewModel = AddressesScreenViewModel(addressRepository, testDispatcher)
        advanceUntilIdle()
        viewModel.onDeleteAddressClicked(address.id!!)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onConfirmDeleteAddress()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { addressRepository.deleteAddress(address.id) }
        assertTrue(viewModel.state.value.snackBarUiState.isVisible)
        assertEquals(SnackBarType.SUCCESS, viewModel.state.value.snackBarUiState.snackBarType)
        assertFalse(viewModel.state.value.deleteDialogUIState.isVisible)
    }

    @Test
    fun `onDismissDeleteDialog() should hide delete dialog`() = runTest {
        coEvery { addressRepository.getUserAddresses() } returns emptyList()
        coEvery { addressRepository.getActiveAddress() } returns null
        viewModel.onDeleteAddressClicked(Uuid.random())
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onDismissDeleteDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.deleteDialogUIState.isVisible)
    }

    @Test
    fun `onDismissSnackBar() should hide snackbar`() = runTest {
        coEvery { addressRepository.getUserAddresses() } throws Exception()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onDismissSnackBar()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.snackBarUiState.isVisible)
    }

    @Test
    fun `onEditAddressClicked() should emit NavigateToAddressDetailsScreen effect`() = runTest {
        coEvery { addressRepository.getUserAddresses() } returns emptyList()
        coEvery { addressRepository.getActiveAddress() } returns null
        val fakeAddressUIState = AddressUIState(
            id = Uuid.random(),
            addressType = AddressType.Home,
            addressDetails = "Test Street 42",
            isMainAddress = true,
            coordinates = CoordinatesUiState(
                latitude = 1.0,
                longitude = 1.0
            )
        )

        viewModel.effect.test {
            viewModel.onEditAddressClicked(fakeAddressUIState)
            val effect = awaitItem() as AddressesScreenUIEffect.NavigateToAddressDetailsScreen
            assertEquals(fakeAddressUIState, effect.addressUIState)
            cancelAndConsumeRemainingEvents()
        }
    }
    @Test
    fun `onConfirmDeleteAddress() should show success snackbar after deletion`() = runTest {
        val address = createFakeAddress(isMain = false)
        coEvery { addressRepository.getUserAddresses() } returns listOf(address.toEntity())
        coEvery { addressRepository.getActiveAddress() } returns null
        coEvery { addressRepository.deleteAddress(any()) } returns Unit

        viewModel = AddressesScreenViewModel(addressRepository, testDispatcher)
        advanceUntilIdle()

        viewModel.onDeleteAddressClicked(address.id!!)
        advanceUntilIdle()

        viewModel.onConfirmDeleteAddress()
        advanceUntilIdle()

        val snackbar = viewModel.state.value.snackBarUiState
        assertTrue(snackbar.isVisible)
        assertEquals(SnackBarType.SUCCESS, snackbar.snackBarType)
    }
    private fun createFakeAddress(isMain: Boolean =true): AddressUIState {
        return AddressUIState(
            id = Uuid.random(),
            addressType = AddressType.Home,
            isMainAddress = isMain,
            addressDetails = "123 Fake St",
            coordinates = CoordinatesUiState(
                latitude = 0.0,
                longitude = 0.0
            )
        )

    }
}
