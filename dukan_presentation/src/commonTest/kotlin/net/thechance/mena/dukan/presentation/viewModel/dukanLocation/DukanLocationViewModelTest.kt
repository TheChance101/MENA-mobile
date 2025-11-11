package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationViewModel.Companion.LATITUDE
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationViewModel.Companion.LONGITUDE
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

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val route = DukanRoute.DukanLocation(latitude = latitude, longitude = longitude)
        savedStateHandle =
            SavedStateHandle(mapOf(LATITUDE to route.latitude, LONGITUDE to route.longitude))
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
    fun `init SHOULD set correct camera position`() = runTest(testDispatcher) {
        val viewModel = DukanLocationViewModel(
            savedStateHandle = savedStateHandle,
            defaultDispatcher = testDispatcher
        )
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(latitude, currentState.cameraPosition.target.latitude)
            assertEquals(longitude, currentState.cameraPosition.target.longitude)
            assertEquals(
                DukanLocationViewModel.DUKAN_LOCATION_ZOOM,
                currentState.cameraPosition.zoom
            )
            cancelAndIgnoreRemainingEvents()
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