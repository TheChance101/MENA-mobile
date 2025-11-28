package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.model.AddressInput
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddressOperationStrategyFactory
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.LocationManagementViewModel
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class LocationManagementViewModelTest {

    private val latitude = 30.12
    private val longitude = 33.14
    private val addressType = AddressType.Home
    private val addressLine = "Cairo"
    private val addressID = Uuid.random()

    private val addressUIState = AddressUIState(
        id = addressID,
        addressType = addressType,
        coordinates = CoordinatesUiState(latitude, longitude),
        addressDetails = addressLine
    )
    private lateinit var viewModel: LocationManagementViewModel
    private val addressesRepository: AddressesRepository = mockk()
    private val saveAddressStrategyFactory: AddressOperationStrategyFactory = mockk()
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            LocationManagementViewModel(
                saveAddressStrategyFactory,
                testDispatcher,
                null
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun ` onClickAddressType() should update address type when it is called`() {

        val newAddressType = AddressType.Home

        viewModel.onClickAddressType(newAddressType)

        assertTrue(viewModel.state.value.addressUIState.addressType == newAddressType)
    }

    @Test
    fun ` onOtherAddressTypeChanged() should update other address type when it is called`() {

        val newAddressType = "Apartment"

        viewModel.onChangeOtherAddressType(newAddressType)

        assertTrue(viewModel.state.value.addressUIState.otherAddressType == newAddressType)
    }

    @Test
    fun `onClickBack() should send NavigateBack effect when it is called`() = runTest {

        viewModel.effect.test {

            viewModel.onClickBack()

            val effect = awaitItem()

            assertTrue(effect is AddEditLocationScreenUIEffect.NavigateBack)

            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun `onClickMap() should send NavigateToMap effect when it is called`() = runTest {

        viewModel.effect.test {

            viewModel.onClickMap()

            val effect = awaitItem()

            assertTrue(effect is AddEditLocationScreenUIEffect.NavigateToMap)

            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun `onClickEdit() should send NavigateToMap effect when it is called`() = runTest {

        viewModel.effect.test {

            viewModel.onClickEdit()

            val effect = awaitItem()

            assertTrue(effect is AddEditLocationScreenUIEffect.NavigateToMap)

            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun `onClickSave() should call createAddress and handle success when addressID is null `() =
        runTest {

            viewModel.onClickAddressType(addressType)

            coEvery { addressesRepository.createAddress(any<AddressInput>()) } returns Unit

            viewModel.effect.test {
                viewModel.onClickSave()
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isInstanceOf(AddEditLocationScreenUIEffect.NavigateBack::class)
            }
        }

    @Test
    fun `onClickSave() should call createAddress and handle error when addressID is null`() =
        runTest {

            val testAddress = AddressUIState(
                id = null,
                addressType = addressType,
                coordinates = CoordinatesUiState(latitude, longitude),
                addressDetails = "Test Address"
            )
            viewModel =
                LocationManagementViewModel(
                    saveAddressStrategyFactory,
                    testDispatcher,
                    testAddress
                )

            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClickAddressType(addressType)
            testDispatcher.scheduler.advanceUntilIdle()

            coEvery { addressesRepository.createAddress(any<AddressInput>()) } throws Exception("Test error")

            viewModel.effect.test(timeout = 1000.milliseconds) {
                viewModel.onClickSave()
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isInstanceOf(AddEditLocationScreenUIEffect.NavigateBack::class)
            }

        }

    @Test
    fun `onClickSave() should call updateAddress and handle success when addressID is not null`() =
        runTest {

            viewModel =
                LocationManagementViewModel(
                    saveAddressStrategyFactory,
                    testDispatcher,
                    addressUIState,
                )
            viewModel.onClickAddressType(addressType)

            coEvery { addressesRepository.updateAddress(any(), any<AddressInput>()) } returns Unit

            viewModel.onClickSave()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(AddEditLocationScreenUIEffect.NavigateBack::class)
            }
        }

    @Test
    fun `onClickSave() should call updateAddress and handle error when addressID is not null`() =
        runTest {

            viewModel =
                LocationManagementViewModel(
                    saveAddressStrategyFactory,
                    testDispatcher,
                    addressUIState
                )
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClickAddressType(addressType)
            testDispatcher.scheduler.advanceUntilIdle()

            coEvery {
                addressesRepository.updateAddress(
                    any(),
                    any<AddressInput>()
                )
            } throws Exception("Test error")

            viewModel.effect.test(timeout = 1000.milliseconds) {
                viewModel.onClickSave()
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(AddEditLocationScreenUIEffect.NavigateBack::class)
            }
        }

    @Test
    fun `changeIsSaveEnabled() should be false when address is empty`() = runTest {

        viewModel.onClickAddressType(addressType)

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false when otherAddressType is empty`() = runTest {

        viewModel.onClickAddressType(AddressType.Other(""))

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false when AddressType is null`() = runTest {

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when data is not changed`() = runTest {

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when otherAddressType is null`() =
        runTest {

            viewModel.onClickAddressType(AddressType.Other(""))

            assertTrue { !viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when otherAddressType is empty`() =
        runTest {

            viewModel.onClickAddressType(AddressType.Other(""))
            viewModel.onChangeOtherAddressType(" ")


            assertTrue { !viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be true in edit mode when addressType is changed`() =
        runTest {
            viewModel =
                LocationManagementViewModel(
                    saveAddressStrategyFactory,
                    testDispatcher,
                    addressUIState
                )

            viewModel.onClickAddressType(AddressType.Office)

            assertTrue { viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be true in edit mode when otherAddressType is changed`() =
        runTest {
            viewModel =
                LocationManagementViewModel(
                    saveAddressStrategyFactory,
                    testDispatcher,
                    addressUIState
                )

            viewModel.onClickAddressType(AddressType.Other("Apartment"))

            viewModel.onChangeOtherAddressType("Apartment")

            assertTrue { viewModel.state.value.isSaveEnabled }

        }
}