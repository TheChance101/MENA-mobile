package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
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
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.MyAddressesScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.MyAddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class MyAddressesScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val addressRepository: AddressesRepository = mockk(relaxed = true)
    private lateinit var viewModel: MyAddressesScreenViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyAddressesScreenViewModel(addressRepository, testDispatcher)
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

        viewModel = MyAddressesScreenViewModel(addressRepository, testDispatcher)

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
            assertTrue(awaitItem() is MyAddressesScreenUIEffect.NavigateBack)
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
                val effect =
                    awaitItem() as MyAddressesScreenUIEffect.NavigateToAddressDetailsScreen
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

        viewModel = MyAddressesScreenViewModel(addressRepository, testDispatcher)
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

        viewModel = MyAddressesScreenViewModel(addressRepository, testDispatcher)
        advanceUntilIdle()
        viewModel.onDeleteAddressClicked(address.id!!)

        testDispatcher.scheduler.advanceUntilIdle()


        viewModel.onConfirmDeleteAddress()


        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(MyAddressesScreenUIEffect.ShowSnackBarSuccess::class)
        }
        assertFalse(viewModel.state.value.deleteDialogUIState.isVisible)

        coVerify { addressRepository.deleteAddress(address.id) }
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
            val effect = awaitItem() as MyAddressesScreenUIEffect.NavigateToAddressDetailsScreen
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

        viewModel = MyAddressesScreenViewModel(addressRepository, testDispatcher)
        advanceUntilIdle()

        viewModel.onDeleteAddressClicked(address.id!!)
        advanceUntilIdle()

        viewModel.onConfirmDeleteAddress()

        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(MyAddressesScreenUIEffect.ShowSnackBarSuccess::class)
        }
    }

    private fun createFakeAddress(isMain: Boolean = true): AddressUIState {
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
