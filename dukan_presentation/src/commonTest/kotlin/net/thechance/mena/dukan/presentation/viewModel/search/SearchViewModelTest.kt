@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.SearchRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SearchViewModelTest {
    private val searchRepository: SearchRepository = mock(MockMode.autofill)
    private val dukanManagementRepository: DukanManagementRepository = mock(MockMode.autofill)
    private lateinit var searchViewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchViewModel = SearchViewModel(
            searchRepository = searchRepository,
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @Test
    fun `onSearchChanged should update state with new query`() = runTest {
        val query = "Puma Shoes"

        searchViewModel.onSearchChanged(query)

        assertEquals(query, searchViewModel.state.value.searchQuery)
    }

    @Test
    fun `onSearchChanged should make searchContentState Complete state when query is not blank`() =
        runTest {
            val query = "Puma Shoes"

            searchViewModel.onSearchChanged(query)

            val expectedContent = SearchUiState.SearchContentState.Complete
            val actualContent = searchViewModel.state.value.searchContentState

            assertEquals(expectedContent, actualContent)
        }

    @Test
    fun `onSearchChanged should make searchContentState Idle state when query is blank`() =
        runTest {
            val query = ""

            searchViewModel.onSearchChanged(query)

            val expectedContent = SearchUiState.SearchContentState.Idle
            val actualContent = searchViewModel.state.value.searchContentState

            assertEquals(expectedContent, actualContent)
        }

    @Test
    fun `onSearchChanged should start searching about dukans`() = runTest(testDispatcher) {
        val query = "Defacto"
        everySuspend {
            searchRepository.findDukansByQuery(
                any(),
                any(),
                any()
            )
        } returns fakeDefactoDukanPaged

        val viewModel = createSearchViewModel()
        viewModel.onSearchChanged(query)

        viewModel.state.test {
            skipItems(1)
            val actualDukanPagingFlow = awaitItem().dukanPagingFlow
            assertNotEquals(actualDukanPagingFlow, emptyFlow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClearSearchClicked should clear searchQuery`() = runTest(testDispatcher) {
        searchViewModel.onClearSearchClicked()

        assertEquals("", searchViewModel.state.value.searchQuery)
    }

    @Test
    fun `onClearSearchClicked should make searchContentState Idle state`() =
        runTest(testDispatcher) {
            searchViewModel.onClearSearchClicked()

            val expectedContent = SearchUiState.SearchContentState.Idle
            val actualContent = searchViewModel.state.value.searchContentState
            assertEquals(expectedContent, actualContent)
        }

    @Test
    fun `onDukansSelected should make UserSelectionSearchList Dukans state`() =
        runTest(testDispatcher) {
            searchViewModel.onClearSearchClicked()

            val expectedContent = SearchUiState.UserSelectionSearchList.Dukans
            val actualContent = searchViewModel.state.value.userSelectionSearchList

            assertEquals(expectedContent, actualContent)
        }

    @Test
    fun `onDukansSelected should search about Dukans`() = runTest(testDispatcher) {
        val query = "Defacto"
        everySuspend {
            searchRepository.findDukansByQuery(
                any(),
                any(),
                any()
            )
        } returns fakeDefactoDukanPaged

        searchViewModel.onSearchChanged(query)
        searchViewModel.onDukansSelected()

        searchViewModel.state.test {
            skipItems(1)
            val actualDukanPagingFlow = awaitItem().dukanPagingFlow
            assertNotEquals(actualDukanPagingFlow, emptyFlow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductsSelected should make UserSelectionSearchList Products state`() =
        runTest(testDispatcher) {
            searchViewModel.onProductsSelected()
            advanceUntilIdle()

            val expectedContent = SearchUiState.UserSelectionSearchList.Products
            val actualContent = searchViewModel.state.value.userSelectionSearchList

            assertEquals(expectedContent, actualContent)
        }

    @Test
    fun `onProductsSelected should search about Products`() = runTest(testDispatcher) {
        val query = "puma-shoes"
        everySuspend {
            searchRepository.findProductsByQuery(
                any(),
                any(),
                any()
            )
        } returns fakePumaShoesProductPaged

        val viewModel = createSearchViewModel()

        viewModel.onSearchChanged(query)
        viewModel.onProductsSelected()
        advanceUntilIdle()

        val actualProductPagingFlow = viewModel.state.value.productPagingFlow
        assertNotEquals(actualProductPagingFlow, emptyFlow())
    }

    @Test
    fun `onDukanFavoriteToggled should update dukanPagingFlow and not show snackbar on success`() = runTest(testDispatcher) {
        val dukanId = Uuid.random()
        val query = "Defacto"

        everySuspend {
            searchRepository.findDukansByQuery(
                any(),
                any(),
                any()
            )
        } returns fakeDefactoDukanPaged

        everySuspend {
            dukanManagementRepository.updateFavoriteDukanStatus(any())
        }

        val viewModel = createSearchViewModel()
        viewModel.onSearchChanged(query)
        advanceUntilIdle()

        viewModel.onDukanFavoriteToggled(dukanId = dukanId, isFavorite = true)
        advanceUntilIdle()

        assertNotEquals(emptyFlow(), viewModel.state.value.dukanPagingFlow)
        assertNull(viewModel.state.value.snackBarUiState)
    }

    @Test
    fun `onBackClicked should emit NavigateBack effect`() = runTest(testDispatcher) {
        searchViewModel.onBackClicked()
        val expectedEffect = SearchEffect.NavigateBack
        val actualEffect = searchViewModel.effect.first()
        assertEquals(expectedEffect, actualEffect)
    }

    @Test
    fun `onProductClicked should emit NavigateToProductDetails effect`() = runTest(testDispatcher) {
        val productId = Uuid.random()
        val dukanId = Uuid.random()
        searchViewModel.onProductClicked(productId,dukanId)
        val expectedEffect = SearchEffect.NavigateToProductDetails(productId = productId.toString(),dukanId = dukanId.toString())
        val actualEffect = searchViewModel.effect.first()
        assertEquals(expectedEffect, actualEffect)

    }

    @Test
    fun `onDukanClicked should emit NavigateToDukanDetails effect`() = runTest(testDispatcher) {
        val dukanId = Uuid.random()
        searchViewModel.onDukanClicked(dukanId)
        val expectedEffect = SearchEffect.NavigateToDukanDetails(dukanId = dukanId.toString())
        val actualEffect = searchViewModel.effect.first()
        assertEquals(expectedEffect, actualEffect)
    }

    @Test
    fun `onSnackBarDismissed should dismiss snackbar by making isInternetConnectionNotAvailable false`() =
        runTest(testDispatcher) {
            searchViewModel.onSnackBarDismissed()

            assertNull(searchViewModel.state.value.snackBarUiState)
        }


    private fun createSearchViewModel(): SearchViewModel {
        return SearchViewModel(
            searchRepository = searchRepository,
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
    }
}