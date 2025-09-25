package net.thechance.mena.dukan.presentation.viewModel.base

import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.presentation.navigation.DukanNavigator
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val navigator = mock<DukanNavigator>(mode = MockMode.autofill)
    private lateinit var viewModel: FakeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = FakeViewModel(dispatcher = dispatcher, navigator = navigator)
    }

    @Test
    fun `updateState SHOULD change state`() = runTest(dispatcher) {
        viewModel.setState("new value")
        assertEquals("new value", viewModel.state.value)
    }

    @Test
    fun `tryToExecute SHOULD update state on success`() = runTest(dispatcher) {
        viewModel.launchSuccess()
        advanceUntilIdle()
        assertEquals("success", viewModel.state.value)
    }

    @Test
    fun `tryToExecute SHOULD update state on error`() = runTest(dispatcher) {
        viewModel.launchFailure()
        advanceUntilIdle()
        assertEquals("error:boom", viewModel.state.value)
    }


    @Test
    fun `tryToCollect SHOULD collect flow values`() = runTest(dispatcher) {
        val flow = flowOf("a", "b", "c")
        viewModel.collectFlow(flow)
        advanceUntilIdle()
        assertEquals("c", viewModel.state.value) // last collected
    }

    @Test
    fun `navigate SHOULD navigate to route`() = runTest(dispatcher) {
        val route = DukanRoute.MainScreenRoute
        viewModel.navigateToScreen(route = route , navOptions = null)
        advanceUntilIdle()
        verifySuspend { navigator.navigate(route) }
    }

    @Test
    fun `navigateUp SHOULD navigate up`() = runTest(dispatcher) {
        viewModel.navigateBack()
        advanceUntilIdle()
        verifySuspend { navigator.navigateUp() }
    }
}
