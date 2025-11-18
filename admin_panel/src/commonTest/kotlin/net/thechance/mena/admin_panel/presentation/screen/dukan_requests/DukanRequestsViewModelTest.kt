package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.admin_panel.domain.entity.dukan.Category
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import kotlin.collections.listOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class DukanRequestsViewModelTest {
    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val stringProvider: StringProvider = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DukanRequestsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        everySuspend {
            dukanRepository.getDukans(any())
        } returns dukansList
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load dukans successfully on initialization`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isLoading)
            assertEquals(dukansList.items.size, currentState.dukans.size)
            assertEquals(dukansList.totalElements, currentState.totalDukanRequests)
            assertNull(currentState.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update sort state when onSortClicked is called with DUKAN_NAME`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DUKAN_NAME)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    DukanRequestsScreenState.SortType.DUKAN_NAME,
                    currentState.sort.type
                )
                assertEquals(
                    DukanRequestsScreenState.SortDirection.DESC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should toggle sort direction when onSortClicked is called twice with same type`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DUKAN_NAME)
            advanceUntilIdle()
            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DUKAN_NAME)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    DukanRequestsScreenState.SortType.DUKAN_NAME,
                    currentState.sort.type
                )
                assertEquals(
                    DukanRequestsScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update sort state when onSortClicked is called with DATE`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DATE)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    DukanRequestsScreenState.SortType.DATE,
                    currentState.sort.type
                )
                assertEquals(
                    DukanRequestsScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should reset to ASC when switching between different sort types`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DUKAN_NAME)
            advanceUntilIdle()
            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DUKAN_NAME)
            advanceUntilIdle()

            viewModel.onSortClicked(DukanRequestsScreenState.SortType.DATE)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    DukanRequestsScreenState.SortType.DATE,
                    currentState.sort.type
                )
                assertEquals(
                    DukanRequestsScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update page and reload dukans when onPageChanged is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val newPage = 3
            viewModel.onPageChanged(newPage)

            viewModel.state.test {
                val currentState = awaitItem()
                skipItems(1)
                advanceUntilIdle()
                assertEquals(newPage, currentState.pageInfo.page)
                assertTrue(currentState.dukans.isNotEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should reload dukans when onRetryClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onRetryClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.dukans.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show dukan details when onViewDetailsClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.isDukanDetailsShown)
            assertEquals(selectedDukan, currentState.selectedDukan)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should hide dukan details when onDismissDukanDetails is called`() = runTest(testDispatcher) {
        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onDukanDetailsDismissed()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isDukanDetailsShown)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should approve dukan successfully when onApproveDukanClicked is called`() = runTest(testDispatcher) {
        everySuspend {
            dukanRepository.updateDukanStatus(any(), any(), any())
        } returns Unit

        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onApproveDukanClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isDukanDetailsShown)
            assertTrue(currentState.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show reject dialog when onRejectDukanClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onRejectDukanClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.isRejectDialogShown)
            assertFalse(currentState.isDukanDetailsShown)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should hide reject dialog when onRejectDukanDialogDismissed is called`() = runTest(testDispatcher) {
        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onRejectDukanClicked()
        advanceUntilIdle()

        viewModel.onRejectDukanDialogDismissed()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isRejectDialogShown)
            assertEquals("", currentState.rejectReason)
            assertFalse(currentState.isRejectButtonLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should reject dukan successfully when onRejectDukanConfirmed is called`() = runTest(testDispatcher) {
        everySuspend {
            dukanRepository.updateDukanStatus(any(), any(), any())
        } returns Unit

        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onRejectDukanClicked()
        advanceUntilIdle()

        viewModel.onRejectionMessageChanged("Does not meet requirements")
        advanceUntilIdle()

        viewModel.onRejectDukanConfirmed()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isRejectDialogShown)
            assertFalse(currentState.isRejectButtonLoading)
            assertTrue(currentState.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update rejection message when onRejectionMessageChanged is called`() = runTest(testDispatcher) {
        initViewModel()

        val testMessage = "Invalid documentation"
        viewModel.onRejectionMessageChanged(testMessage)
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(testMessage, currentState.rejectReason)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not update rejection message when it exceeds 200 characters`() = runTest(testDispatcher) {
        initViewModel()

        val longMessage = "a".repeat(201)
        viewModel.onRejectionMessageChanged(longMessage)
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals("", currentState.rejectReason)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update rejection message when it is exactly 199 characters`() = runTest(testDispatcher) {
        initViewModel()

        val validMessage = "a".repeat(199)
        viewModel.onRejectionMessageChanged(validMessage)
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(validMessage, currentState.rejectReason)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle error when approving dukan fails`() = runTest(testDispatcher) {
        everySuspend {
            dukanRepository.updateDukanStatus(any(), any(), any())
        } throws Exception("Network error")

        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onApproveDukanClicked()
        testScheduler.advanceTimeBy(100L)

        val currentState = viewModel.state.value
        assertFalse(currentState.snackBar.isSuccess)
        assertTrue(currentState.snackBar.isVisible)
        assertTrue(currentState.isDukanDetailsShown)
    }

    @Test
    fun `should handle error when rejecting dukan fails`() = runTest(testDispatcher) {
        everySuspend {
            dukanRepository.updateDukanStatus(any(), any(), any())
        } throws Exception("Network error")

        initViewModel()

        val selectedDukan = viewModel.state.value.dukans.first()
        viewModel.onViewDetailsClicked(selectedDukan)
        advanceUntilIdle()

        viewModel.onRejectDukanClicked()
        advanceUntilIdle()

        viewModel.onRejectDukanConfirmed()
        testScheduler.advanceTimeBy(100L)

        val currentState = viewModel.state.value
        assertFalse(currentState.snackBar.isSuccess)
        assertTrue(currentState.snackBar.isVisible)
    }

    private fun TestScope.initViewModel() {
        viewModel = DukanRequestsViewModel(
            dukanRepository = dukanRepository,
            dispatcher = testDispatcher,
            stringProvider = stringProvider
        )
        advanceUntilIdle()
    }

    private companion object {
        val dukansList = PagedResult(
            items = listOf(
                Dukan(
                    id = Uuid.random(),
                    name = "mesh araref",
                    imageUrl = "",
                    address = "mesh araref",
                    latitude = 77.7,
                    longitude = 77.7,
                    createdAt = LocalDateTime(2025, 10, 15, 23, 59, 59),
                    categories = listOf(
                        Category(
                            id = Uuid.random(),
                            title = "mesh araref"
                        )
                    ),
                    activationStatus = Dukan.ActivationStatus.ACTIVATED,
                    status = Dukan.Status.PENDING
                ),
                Dukan(
                    id = Uuid.random(),
                    name = "mesh araref",
                    imageUrl = "",
                    address = "mesh araref",
                    latitude = 77.7,
                    longitude = 77.7,
                    createdAt = LocalDateTime(2025, 10, 15, 23, 59, 59),
                    categories = listOf(
                        Category(
                            id = Uuid.random(),
                            title = "mesh araref"
                        )
                    ),
                    activationStatus = Dukan.ActivationStatus.ACTIVATED,
                    status = Dukan.Status.PENDING
                ),
                Dukan(
                    id = Uuid.random(),
                    name = "mesh araref",
                    imageUrl = "",
                    address = "mesh araref",
                    latitude = 77.7,
                    longitude = 77.7,
                    createdAt = LocalDateTime(2025, 10, 15, 23, 59, 59),
                    categories = listOf(
                        Category(
                            id = Uuid.random(),
                            title = "mesh araref"
                        )
                    ),
                    activationStatus = Dukan.ActivationStatus.ACTIVATED,
                    status = Dukan.Status.PENDING
                )
            ),
            totalPages = 7,
            currentPage = 7,
            totalElements = 7
        )
    }
}