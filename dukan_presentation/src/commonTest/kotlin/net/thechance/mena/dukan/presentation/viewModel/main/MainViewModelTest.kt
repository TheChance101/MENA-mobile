package net.thechance.mena.dukan.presentation.viewModel.main

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dukanManagementRepository: DukanManagementRepository
    private lateinit var dukanDiscoveryRepository: DukanDiscoveryRepository
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dukanManagementRepository = mock<DukanManagementRepository>(mode = MockMode.autofill)
        dukanDiscoveryRepository = mock<DukanDiscoveryRepository>(mode = MockMode.autofill)
        everySuspend { dukanManagementRepository.getMyDukanStatus() } returns null
        everySuspend { dukanManagementRepository.getCategories() } returns emptyList()
        mainViewModel =
            MainViewModel(dukanManagementRepository, dukanDiscoveryRepository, testDispatcher)
        everySuspend {
            dukanManagementRepository.updateFavoriteDukanStatus(
                fakeDukans[0].id.toString()
            )
        }

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
            val updated = awaitItem()
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
    fun `onViewMoreButtonClick SHOULD emit NavigateToDukansCategoriesScreen`() = runTest {

        mainViewModel.onViewMoreClicked()
        val actualEffect = mainViewModel.effect.first()
        val expectedEffect = MainScreenEffect.NavigateToDukansCategoriesScreen

        assertEquals(expectedEffect, actualEffect)

    }

    @Test
    fun `onSelectedCategoryClicked SHOULD emit NavigateToDukansScreenByCategory with correct categoryId`() =
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
    fun `onNearestDukanClick should emit NavigateToSelectedDukan effect with correct dukanId`() =
        runTest {
            val dukanId = "1"

            mainViewModel.onNearestDukanClicked(dukanId)

            val actualEffect = mainViewModel.effect.first()
            val expectedEffect = MainScreenEffect.NavigateToSelectedDukan(dukanId)
            assertEquals(expectedEffect, actualEffect)
        }

    @Test
    fun `onEditorPickDukanClick should emit NavigateToSelectedDukan effect with correct dukanId`() =
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

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onFavoriteDukanClicked should toggle isFavorite`() = runTest {
        val fakeDukan1 = fakeDukans[0]

        everySuspend {
            dukanDiscoveryRepository.getEditorPicksDukans(
                any(),
                any()
            )
        } returns PagedResult(
            items = listOf(fakeDukan1),
            currentPage = 1,
            totalItems = 1,
            pageSize = 10,
            totalPages = 1
        )

        mainViewModel =
            MainViewModel(dukanManagementRepository, dukanDiscoveryRepository, testDispatcher)
        advanceUntilIdle()

        mainViewModel.onFavoriteDukanClicked(fakeDukan1.id.toString())
        advanceUntilIdle()

        val updatedDukan1 = mainViewModel.state.value.editorPickDukans.asSnapshot()
        assertEquals(true, updatedDukan1[0].isFavorite)
    }

    @OptIn(ExperimentalUuidApi::class)
    val fakeDukans = listOf(
        Dukan(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174002"),
            name = "Tech Hub",
            imageUrl = "https://example.com/tech.jpg",
            categories = emptySet(),
            isFavorite = false,
            coordinates = Dukan.Coordinates(
                latitude = 12.34,
                longitude = 56.78
            ),
            address = "123 Tech Street",
            status = Dukan.Status.PENDING,
            color = net.thechance.mena.dukan.domain.entity.Color(
                id = Uuid.random(),
                hexCode = "#FF5733"
            ),
            style = Dukan.Style.WIDE_IMAGE
        )
    )
    @Test
    fun `onSearchButtonClicked SHOULD emit NavigateToSearchScreen`() = runTest {
        mainViewModel.onSearchButtonClicked()

        val actualEffect = mainViewModel.effect.first()
        val expectedEffect = MainScreenEffect.NavigateToSearchScreen

        assertEquals(expectedEffect, actualEffect)
    }

}