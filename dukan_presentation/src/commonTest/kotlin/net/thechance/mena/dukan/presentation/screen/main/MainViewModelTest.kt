package net.thechance.mena.dukan.presentation.screen.main

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.MyDukanStatus
import net.thechance.mena.dukan.domain.exceptions.DukanNotFoundException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `When a user doesnt have dukan then the DukanStatusUi should be None`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.getMyDukanStatus() } returns null

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                val init = awaitItem()
                assertEquals(
                    expected = MainScreenUiState.DukanState(
                        name = "",
                        status = DukanStatusUi.None
                    ),
                    actual = init.dukanState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When a user has dukan then the DukanStatusUi should be the current dukan status with current dukan name`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.getMyDukanStatus() } returns MyDukanStatus(
                status = Dukan.Status.PENDING,
                dukanName = "Dukan El Sa3ada"
            )

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                awaitItem()
                val secondEmit = awaitItem()
                assertEquals(
                    expected = MainScreenUiState.DukanState(
                        name = "Dukan El Sa3ada",
                        status = DukanStatusUi.Pending
                    ),
                    actual = secondEmit.dukanState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When getMyDukanStatus throws DukanNotFoundException the MainViewModelUiState should set ErrorMessage to null`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.getMyDukanStatus() } throws DukanNotFoundException()

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                val result = awaitItem()
                assertEquals(null, result.errorMessage)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the user doesnt have Dukan and clicks on the Dukan button, then it should emit NavigateToAddDukanScreen`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.getMyDukanStatus() } returns null

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )
            advanceUntilIdle()

            mainViewModel.onDukanButtonClicked()

            mainViewModel.effect.test {
                val currentEffect = awaitItem()
                assertEquals(MainEffect.NavigateToAddDukanScreen, currentEffect)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the dukanStatusUi is pending and user clicks on the Dukan button, then it should emit NavigateToPendingDukanScreen`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.getMyDukanStatus() } returns MyDukanStatus(
                status = Dukan.Status.PENDING,
                dukanName = "Dukan El Sa3ada"
            )

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )
            advanceUntilIdle()

            mainViewModel.onDukanButtonClicked()

            mainViewModel.effect.test {
                val currentEffect = awaitItem()
                assertEquals(MainEffect.NavigateToPendingDukanScreen, currentEffect)
                cancelAndIgnoreRemainingEvents()
            }
        }

}
