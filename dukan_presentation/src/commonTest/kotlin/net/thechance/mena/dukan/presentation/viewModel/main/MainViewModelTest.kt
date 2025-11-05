package net.thechance.mena.dukan.presentation.viewModel.main

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dukanManagementRepository: DukanManagementRepository
    private lateinit var dukanDiscoveryRepository: DukanDiscoveryRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dukanManagementRepository = mock<DukanManagementRepository>(mode = MockMode.autofill)
        dukanDiscoveryRepository = mock<DukanDiscoveryRepository>(mode = MockMode.autofill)
        everySuspend { dukanManagementRepository.getMyDukanStatus() } returns null
        everySuspend { dukanManagementRepository.getCategories() } returns emptyList()
        mainViewModel =
            MainViewModel(dukanManagementRepository, dukanDiscoveryRepository, testDispatcher)
    }

    @Test
    fun `When a user doesnt have dukan then the DukanStatusUi should be None`() = runTest {

        val mainViewModel = MainViewModel(
            dukanManagementRepository = dukanManagementRepository,
            dukanDiscoveryRepository = dukanDiscoveryRepository,
            dispatcher = testDispatcher
        )

        advanceUntilIdle()
        mainViewModel.state.test {
            val updated = awaitItem() // state after repository returns null
            assertEquals(
                expected = MainScreenUiState.DukanState(
                    name = "",
                    status = MainScreenUiState.DukanStatusUi.None
                ),
                actual = updated.dukanState
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When a user has dukan then the DukanStatusUi should be the current dukan status with current dukan name`() =
        runTest {
            everySuspend { dukanManagementRepository.getMyDukanStatus() } returns MyDukanStatus(
                status = Dukan.Status.PENDING,
                dukanName = "Dukan El Sa3ada"
            )

            val mainViewModel = MainViewModel(
                dukanManagementRepository = dukanManagementRepository,
                dukanDiscoveryRepository = dukanDiscoveryRepository,
                dispatcher = testDispatcher
            )

            advanceUntilIdle()
            mainViewModel.state.test {
                val secondEmit = awaitItem()
                assertEquals(
                    expected = MainScreenUiState.DukanState(
                        name = "Dukan El Sa3ada",
                        status = MainScreenUiState.DukanStatusUi.Pending
                    ),
                    actual = secondEmit.dukanState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When getMyDukanStatus throws DukanNotFoundException the MainViewModelUiState should set ErrorMessage to null`() =
        runTest {
            everySuspend { dukanManagementRepository.getMyDukanStatus() } throws NoSuchItemException()

            advanceUntilIdle()
            mainViewModel.state.test {
                val result = awaitItem()
                assertEquals(
                    MainScreenUiState.DukanState(
                        status =
                            MainScreenUiState.DukanStatusUi.None
                    ), result.dukanState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the user doesnt have Dukan and clicks on the Dukan button, then it should emit NavigateToAddDukanScreen`() =
        runTest {
            mainViewModel.updateState {
                copy(
                    dukanState = MainScreenUiState.DukanState(
                        status = MainScreenUiState.DukanStatusUi.None
                    )
                )
            }
            mainViewModel.onDukanButtonClicked()

            mainViewModel.effect.test {
                val currentEffect = awaitItem()
                assertEquals(MainScreenEffect.NavigateToAddDukanScreen, currentEffect)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the dukanStatusUi is pending and user clicks on the Dukan button, then it should emit NavigateToPendingDukanScreen`() =
        runTest {
            everySuspend { dukanManagementRepository.getMyDukanStatus() } returns MyDukanStatus(
                status = Dukan.Status.PENDING,
                dukanName = "Dukan El Sa3ada"
            )

            val mainViewModel = MainViewModel(
                dukanManagementRepository = dukanManagementRepository,
                dukanDiscoveryRepository = dukanDiscoveryRepository,
                dispatcher = testDispatcher
            )
            advanceUntilIdle()

            mainViewModel.onDukanButtonClicked()

            mainViewModel.effect.test {
                val currentEffect = awaitItem()
                assertEquals(MainScreenEffect.NavigateToPendingDukanScreen, currentEffect)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When the dukanStatusUi is approved and user clicks on the Dukan button, then it should emit NavigateToManageDukanScreen`() =
        runTest {
            everySuspend { dukanManagementRepository.getMyDukanStatus() } returns MyDukanStatus(
                status = Dukan.Status.APPROVED,
                dukanName = "Dukan El Sa3ada"
            )
            val mainViewModel = MainViewModel(
                dukanManagementRepository = dukanManagementRepository,
                dukanDiscoveryRepository = dukanDiscoveryRepository,
                dispatcher = testDispatcher
            )
            advanceUntilIdle()

            mainViewModel.onDukanButtonClicked()

            mainViewModel.effect.test {
                val currentEffect = awaitItem()
                assertEquals(MainScreenEffect.NavigateToManageDukanScreen, currentEffect)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `When getCategories throws an exception then snackBar state will be updated`() = runTest {
        everySuspend { dukanManagementRepository.getCategories() } throws Exception()

        mainViewModel = MainViewModel(
            dukanManagementRepository = dukanManagementRepository,
            dukanDiscoveryRepository = dukanDiscoveryRepository,
            dispatcher = testDispatcher
        )

        mainViewModel.state.test {
            awaitItem()

            val updated = awaitItem()

            assertEquals(
                null,
                updated.snackBarState
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onViewMoreButtonClick SHOULD emit NavigateCategoryToScreen`() = runTest {

        mainViewModel.onViewMoreClicked()
        val actualEffect = mainViewModel.effect.first()
        val expectedEffect = MainScreenEffect.NavigateToDukansCategoriesScreen

        assertEquals(expectedEffect, actualEffect)

    }

    @Test
    fun `onCategorySelectedClick SHOULD emit NavigateToDukansScreenByCategory with correct categoryId`() =
        runTest {
            val categoryId = "1"
            val categoryName = "Category 1"

            mainViewModel.onSelectedCategoryClicked(categoryId, categoryName)
            val actualEffect = mainViewModel.effect.first()
            val expectedEffect =
                MainScreenEffect.NavigateToDukansScreenByCategory(categoryId, categoryName)
            assertEquals(expectedEffect, actualEffect)
        }

    @Test
    fun `onNearestDukanClick should emit NavigateSelectedNearsetDukan effect with correct dukanId`() =
        runTest {
            val dukanId = "1"

            mainViewModel.onNearestDukanClicked(dukanId)

            val actualEffect = mainViewModel.effect.first()
            val expectedEffect = MainScreenEffect.NavigateToSelectedDukan(dukanId)
            assertEquals(expectedEffect, actualEffect)
        }

    @Test
    fun `onEditorPickDukanClick should emit NavigateSelectedDukan effect with correct dukanId`() =
        runTest {
            val dukanId = "1"

            mainViewModel.onEditorPickDukanClicked(dukanId)

            val actualEffect = mainViewModel.effect.first()
            val expectedEffect = MainScreenEffect.NavigateToSelectedDukan(dukanId)
            assertEquals(expectedEffect, actualEffect)
        }

    @Test
    fun `When getMyDukanStatus throws DukanNotFoundException then dukanState should be None and errorMessage should be set`() =
        runTest {
            everySuspend { dukanManagementRepository.getMyDukanStatus() } throws NoSuchItemException(
                "Dukan not found"
            )
            everySuspend { dukanManagementRepository.getCategories() } returns emptyList()

            mainViewModel = MainViewModel(
                dukanManagementRepository = dukanManagementRepository,
                dukanDiscoveryRepository = dukanDiscoveryRepository,
                dispatcher = testDispatcher
            )
            advanceUntilIdle()

            mainViewModel.state.test {
                val result = awaitItem()
                assertEquals(
                    MainScreenUiState.DukanState(
                        status = MainScreenUiState.DukanStatusUi.None
                    ),
                    result.dukanState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

}