@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    private lateinit var searchViewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        searchViewModel = SearchViewModel(
            searchRepository = searchRepository,
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

        searchViewModel.onSearchChanged(query)
        advanceUntilIdle()

        searchViewModel.state.test {
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

        searchViewModel.onSearchChanged(query)
        searchViewModel.onProductsSelected()

        searchViewModel.state.test {
            skipItems(1)
            val actualProductPagingFlow = awaitItem().productPagingFlow
            assertNotEquals(actualProductPagingFlow, emptyFlow())
            cancelAndIgnoreRemainingEvents()
        }
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
        searchViewModel.onProductClicked(productId)
        val expectedEffect = SearchEffect.NavigateToProductDetails(productId = productId.toString())
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

    @Test
    fun `onRetryClicked should reSearch about last query`() = runTest(testDispatcher) {
        val lastQuery = "Defacto"
        everySuspend {
            searchRepository.findDukansByQuery(
                any(),
                any(),
                any()
            )
        } returns fakeDefactoDukanPaged

        searchViewModel.onSearchChanged(lastQuery)
        searchViewModel.onRetryClicked()

        searchViewModel.state.test {
            skipItems(1)
            val actualDukanPagingFlow = awaitItem().dukanPagingFlow
            assertNotEquals(actualDukanPagingFlow, emptyFlow())
            cancelAndIgnoreRemainingEvents()
        }
    }
}