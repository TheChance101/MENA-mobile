package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DukanLocationViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DukanLocationViewModel

    private val latitude = 30.0
    private val longitude = 31.0


    private fun createViewModel() = DukanLocationViewModel(
        savedStateHandle = savedStateHandle,
        defaultDispatcher = testDispatcher
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val route = DukanRoute.DukanLocation(latitude = latitude, longitude = longitude)
        savedStateHandle = SavedStateHandle(mapOf("latitude" to route.latitude, "longitude" to route.longitude))
        viewModel = DukanLocationViewModel(
            savedStateHandle = savedStateHandle,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD set correct camera position`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val updatedState = awaitItem()
            assertEquals(latitude, updatedState.cameraPosition.target.latitude)
            assertEquals(longitude, updatedState.cameraPosition.target.longitude)
            assertEquals(DukanLocationViewModel.DUKAN_LOCATION_ZOOM, updatedState.cameraPosition.zoom)
        }
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            assertEquals(DukanLocationEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}