package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.repository.AddressesRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditLocationScreenViewModelTest {

    private val latitude = 30.12
    private val longitude = 33.14
    private val addressType = AddressType.Home
    private val addressLine = "Cairo"
    private val addressID = "126b8069-b5f7-4148-934a-7a5f10b7194f"

    private lateinit var viewModel: AddEditLocationScreenViewModel
    private val addressesRepository: AddressesRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddEditLocationScreenViewModel(addressesRepository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAddressChanged() should update address when it is called`() {

        val newAddress = "Nasr City"

        viewModel.onChangeAddress(newAddress)

        assertTrue(viewModel.state.value.address == newAddress)
    }

    @Test
    fun ` onClickAddressType() should update address type when it is called`() {

        val newAddressType = AddressType.Home

        viewModel.onClickAddressType(newAddressType)

        assertTrue(viewModel.state.value.addressType == newAddressType)
    }

    @Test
    fun ` onOtherAddressTypeChanged() should update other address type when it is called`() {

        val newAddressType = "Apartment"

        viewModel.onChangeOtherAddressType(newAddressType)

        assertTrue(viewModel.state.value.otherAddress == newAddressType)
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

            viewModel.onChangeAddress(addressLine)

            coEvery { addressesRepository.createAddress(any()) } returns Unit

            viewModel.effect.test {

                viewModel.onClickSave()

                val effect = awaitItem()

                assertTrue(effect is AddEditLocationScreenUIEffect.NavigateBack)

                cancelAndConsumeRemainingEvents()

            }
        }

    @Test
    fun `onClickSave() should call createAddress and handle error when addressID is null`() =
        runTest {

            viewModel.onClickAddressType(addressType)

            viewModel.onChangeAddress(addressLine)

            coEvery { addressesRepository.createAddress(any()) } throws UnAuthorizedException()

            viewModel.onClickSave()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.errorMessage != null }
        }

    @Test
    fun `onClickSave() should call editAddress and handle success when addressID is not null`() =
        runTest {

            viewModel.onClickAddressType(addressType)

            viewModel.onChangeAddress(addressLine)

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )

            coEvery { addressesRepository.editAddress(any()) } returns Unit

            viewModel.effect.test {

                viewModel.onClickSave()

                val effect = awaitItem()

                assertTrue(effect is AddEditLocationScreenUIEffect.NavigateBack)

                cancelAndConsumeRemainingEvents()

            }
        }

    @Test
    fun `onClickSave() should call editAddress and handle error when addressID is not null`() =
        runTest {

            viewModel.onClickAddressType(addressType)

            viewModel.onChangeAddress(addressLine)

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )

            coEvery { addressesRepository.editAddress(any()) } throws UnAuthorizedException()

            viewModel.onClickSave()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue { viewModel.state.value.errorMessage != null }
        }

    @Test
    fun `changeIsSaveEnabled() should be false when address is empty`() = runTest {
        viewModel.onChangeAddress(" ")

        viewModel.onClickAddressType(addressType)

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false when otherAddressType is null`() = runTest {
        viewModel.onChangeAddress(addressLine)

        viewModel.onClickAddressType(AddressType.Other)

        viewModel.onChangeOtherAddressType(" ")

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false when otherAddressType is empty`() = runTest {
        viewModel.onChangeAddress(addressLine)

        viewModel.onClickAddressType(AddressType.Other)

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false when AddressType is null`() = runTest {
        viewModel.onChangeAddress(addressLine)

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be true when address data is valid`() = runTest {
        viewModel.onChangeAddress(addressLine)

        viewModel.onClickAddressType(addressType)

        assertTrue { viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when data is not changed`() = runTest {

        viewModel.setInitialAddressData(
            addressID = addressID,
            address = addressLine,
            latitude = latitude,
            longitude = longitude,
            addressType = addressType,
            otherAddress = null,
            isActive = false
        )

        assertTrue { !viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when otherAddressType is null`() =
        runTest {

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )
            viewModel.onClickAddressType(AddressType.Other)


            assertTrue { !viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be false in edit mode when otherAddressType is empty`() =
        runTest {

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )
            viewModel.onClickAddressType(AddressType.Other)
            viewModel.onChangeOtherAddressType(" ")


            assertTrue { !viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be true in edit mode when addressType is changed`() =
        runTest {

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )
            viewModel.onClickAddressType(AddressType.Office)

            assertTrue { viewModel.state.value.isSaveEnabled }

        }

    @Test
    fun `changeIsSaveEnabled() should be true in edit mode when address is changed`() = runTest {

        viewModel.setInitialAddressData(
            addressID = addressID,
            address = addressLine,
            latitude = latitude,
            longitude = longitude,
            addressType = addressType,
            otherAddress = null,
            isActive = false
        )
        viewModel.onChangeAddress("Giza")

        assertTrue { viewModel.state.value.isSaveEnabled }

    }

    @Test
    fun `changeIsSaveEnabled() should be true in edit mode when otherAddressType is changed`() =
        runTest {

            viewModel.setInitialAddressData(
                addressID = addressID,
                address = addressLine,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                otherAddress = null,
                isActive = false
            )
            viewModel.onClickAddressType(AddressType.Other)
            viewModel.onChangeOtherAddressType("Apartment")

            assertTrue { viewModel.state.value.isSaveEnabled }

        }


}