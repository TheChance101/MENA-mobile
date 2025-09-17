package net.thechance.mena.dukan.presentation.screen.home

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
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.DukanNotFoundException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `When a user doesnt have dukan then the DukanStatusUi should be None`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.isUserHasDukan() } returns false

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                val init = awaitItem()
                assertEquals(MainScreenUiState.DukanStatusUi.None, init.dukanStatus)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When a user has dukan then the DukanStatusUi should be the current dukan status`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.isUserHasDukan() } returns true
            everySuspend { dukanRepository.getMyDukan() } returns dummyDukan

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                awaitItem()
                val secondEmit = awaitItem()
                assertEquals(dummyDukan.status.toUiState(), secondEmit.dukanStatus)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When getMyDukan throws DukanNotFoundException then the MainViewModelUiState should update ErrorMessage`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.isUserHasDukan() } returns true
            everySuspend { dukanRepository.getMyDukan() } throws DukanNotFoundException("Dukan not found")

            val mainViewModel = MainViewModel(
                dukanRepository = dukanRepository,
                dispatcher = testDispatcher
            )

            mainViewModel.state.test {
                awaitItem()
                val secondEmit = awaitItem()
                assertEquals("Dukan not found", secondEmit.errorMessage)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the user doesnt have Dukan and clicks on the Dukan button, then it should emit NavigateToAddDukanScreen`() =
        runTest(testDispatcher) {
            everySuspend { dukanRepository.isUserHasDukan() } returns false

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
            everySuspend { dukanRepository.isUserHasDukan() } returns true
            everySuspend { dukanRepository.getMyDukan() } returns dummyDukan

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

private val dummyDukan = Dukan(
    id = "1",
    name = "Dukan El Sa3ada",
    imageUrl = "https://picsum.photos/200/300",
    categories = setOf(
        Category(
            id = "c1",
            name = "Grocery",
            imageUrl = "https://picsum.photos/100/100?1"
        ),
        Category(
            id = "c2",
            name = "Bakery",
            imageUrl = "https://picsum.photos/100/100?2"
        )
    ),
    coordinates = Dukan.Coordinates(
        latitude = 30.0444,
        longitude = 31.2357
    ),
    address = "123 Cairo Street, Egypt",
    status = Dukan.Status.Pending,
    color = 0xFF2196F3,
    style = Dukan.Style.WIDE_IMAGE
)
