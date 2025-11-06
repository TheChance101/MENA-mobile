package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen.EnableLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class EnableLocationScreenViewModelTest {
    private val locationPermissionHandler = mockk<PermissionHandler>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: EnableLocationScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EnableLocationScreenViewModel(locationPermissionHandler, testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onClickBack should send NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            testDispatcher.scheduler.advanceUntilIdle()

            val emittedEffect = awaitItem()
            assert(emittedEffect is EnableLocationScreenUIEffect.NavigateBack)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onClickEnablePermission should call openSettings`() = runTest {
        coEvery { locationPermissionHandler.openSettingPage() } just Runs

        viewModel.onClickEnablePermission()

        verify { locationPermissionHandler.openSettingPage() }
    }
}