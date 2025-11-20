package net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CalibrateDeviceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var viewModel: CalibrateDeviceViewModel

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }
        Dispatchers.setMain(testDispatcher)
        viewModel = CalibrateDeviceViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = testScope.runTest {
        viewModel.uiEffect.test {
            viewModel.onBackClick()
            advanceUntilIdle()
            assertEquals(CalibrateDeviceEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onContinueClick should emit NavigateToQiblah effect`() = testScope.runTest {
        viewModel.uiEffect.test {
            viewModel.onContinueClick()
            advanceUntilIdle()
            assertEquals(CalibrateDeviceEffect.NavigateToQiblah, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}