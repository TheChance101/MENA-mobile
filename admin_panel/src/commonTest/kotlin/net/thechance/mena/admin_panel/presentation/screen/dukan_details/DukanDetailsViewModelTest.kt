@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DukanDetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load dukan details on initialization`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

        initViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(dukanItemUiState.id, state.dukan.id)
            assertFalse(state.isDukanDetailsLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should load shelves after dukan details`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

        initViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.shelves.isNotEmpty())
            assertEquals(1, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateBack effect when onBackBtnClicked is called`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

            initViewModel()

            viewModel.uiEffect.test {
                viewModel.onBackButtonClicked()

                val effect = awaitItem()
                assertTrue(effect is DukanDetailEffect.NavigateBack)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should load products when a shelf is selected`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

        initViewModel()
        advanceUntilIdle()

        val shelfId = shelves.first().id
        viewModel.onShelfSelected(shelfId)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.products.isNotEmpty())   // loaded products
            assertEquals(products.size, state.products.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should request next products page when onNextProductsPageRequested is called`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

            initViewModel()
            advanceUntilIdle()

            viewModel.onNextProductsPageRequested()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.products.isNotEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should retry loading dukan and shelves on onRetry`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

        initViewModel()
        advanceUntilIdle()

        viewModel.onRetry()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.shelves.isNotEmpty())
            assertFalse(state.isDukanDetailsLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should open deactivate dialog when onChangeDukanStatusBtnClicked`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

            initViewModel()
            advanceUntilIdle()

            viewModel.onChangeDukanStatusButtonClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.isDeactivateDukanDialogShown)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should close deactivate dialog when onDeactivateDukanDialogDismissed`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

            initViewModel()
            advanceUntilIdle()

            viewModel.onChangeDukanStatusButtonClicked()
            advanceUntilIdle()

            viewModel.onDeactivateDukanDialogDismissed()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertFalse(state.isDeactivateDukanDialogShown)
                assertEquals("", state.deactivateReason)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update deactivate reason when onDeactivateReasonChanged`() = runTest(testDispatcher) {

        everySuspend { dukanRepository.getDukanDetails() } returns dukan
        everySuspend { dukanRepository.getDukanShelves(any(), any(), any()) } returns
                PagedResult(items = shelves, totalPages = 1, currentPage = 0, totalElements = shelves.size)

        everySuspend { dukanRepository.getShelfProducts(any(), any(), any()) } returns
                PagedResult(items = products, totalPages = 1, currentPage = 0, totalElements = products.size)

            initViewModel()
            advanceUntilIdle()

            viewModel.onDeactivateReasonChanged("Test reason")
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals("Test reason", state.deactivateReason)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun TestScope.initViewModel() {
        viewModel = DukanDetailsViewModel(
            dukanRepository = dukanRepository,
            stringProvider = stringProvider,
            dispatcher = testDispatcher
        )
        advanceUntilIdle()
    }

    private companion object {
        val dukanId = Uuid.random()
        val dukanName = "dukan 1"
        val dukanImg = "image url 1"
        val dukanAddress = "address 1"

        val dukan = Dukan(
            id = dukanId,
            name = dukanName,
            imageUrl = dukanImg,
            address = dukanAddress,
            latitude = 11.0,
            longitude = 12.0,
            createdAt = LocalDateTime(2020, 12, 12, 12, 12),
            categories = listOf(),
            activationStatus = Dukan.ActivationStatus.ACTIVATED,
            status = Dukan.Status.APPROVED
        )

        val shelves = listOf(
            Shelf(id = Uuid.random(), title = "shelf 1")
        )

        val products = listOf(
            Product(
                id = Uuid.random(),
                name = "product 1",
                finalPrice = 22.0,
                basePrice = 10.0,
                description = "description 1",
                imageUrls = listOf()
            )
        )

        val dukanItemUiState = DukanDetailsScreenState.DukanItemUiState(
            id = dukanId,
            name = dukanName,
            address = dukanAddress,
            imageUrl = dukanImg,
            categories = listOf(),
            dukanStatus = DukanDetailsScreenState.DukanStatus.ACTIVE
        )
    }
}
