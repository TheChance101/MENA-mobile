package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddLocationScreenViewModelTest {

    private lateinit var viewModel: AddLocationScreenViewModel
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        viewModel = AddLocationScreenViewModel(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun`should update address when onAddressChanged() is called`() {

        val newAddress = "Nasr City"

        viewModel.onAddressChanged(newAddress)

        assertTrue(viewModel.state.value.address == newAddress)
    }

    @Test
    fun`should update address type when onClickAddressType() is called`() {

        val newAddressType = AddressType.Home

        viewModel.onClickAddressType(newAddressType)

        assertTrue(viewModel.state.value.addressType == newAddressType)
    }

    @Test
    fun`should update other address type when onOtherAddressTypeChanged() is called`() {

        val newAddressType = "Apartment"

        viewModel.onOtherAddressTypeChanged(newAddressType)

        assertTrue(viewModel.state.value.otherAddress == newAddressType)
    }

    @Test
    fun`should send NavigateBack effect when onClickBack() is called`() = runTest{

        viewModel.effect.test {

            viewModel.onClickBack()

            val effect = awaitItem()

            assertTrue(effect is AddLocationScreenUIEffect.NavigateBack)

            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun`should send NavigateToMap effect when onClickMap() is called`() = runTest{

        viewModel.effect.test {

            viewModel.onClickMap()

            val effect = awaitItem()

            assertTrue(effect is AddLocationScreenUIEffect.NavigateToMap)

            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun`should send NavigateToMap effect when onClickEdit() is called`() = runTest{

        viewModel.effect.test {

            viewModel.onClickEdit()

            val effect = awaitItem()

            assertTrue(effect is AddLocationScreenUIEffect.NavigateToMap)

            cancelAndConsumeRemainingEvents()

        }
    }

}