package net.thechance.mena.trends.presentation.screen.update_categories

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.utils.TestExtensions
import net.thechance.mena.trends.presentation.utils.categories
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdateCategoriesViewModelTest : TestExtensions() {
    private val repository: CategoryRepository = mock<CategoryRepository>(mode = MockMode.autofill)
    private val viewModel by lazy {
        UpdateCategoriesViewModel(
            repository = repository,
            defaultDispatcher = testDispatcher
        )
    }

    @BeforeTest
    fun setup() {
        everySuspend { repository.getAllCategories() } returns categories
    }

    @Test
    fun `getCategories should start loading when initially called`() =
        runTest(testDispatcher) {
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.isLoading)
            }
        }

    @Test
    fun `getCategories should return categories with success when repository returns value`() =
        runTest(testDispatcher) {
            viewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertEquals(categories.size, state.categories.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCategories should throw exception when initially called`() =
        runTest(testDispatcher) {
            everySuspend { repository.getAllCategories() } throws Exception()

            viewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertThat(state.errorState).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCategories should end loading when initially called`() =
        runTest(testDispatcher) {
            viewModel.state.test {
                skipItems(2)
                val state = awaitItem()
                assertFalse(state.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickCategory should toggle category selection state`() = runTest(testDispatcher) {
        viewModel.state.test {
            skipItems(2)
            val state = awaitItem()
            val firstCategory = state.categories.first()
            firstCategory.value.id?.let {
                viewModel.onClickCategory(it)
            }

            val updatedState = awaitItem()
            val updatedFirstCategory =
                updatedState.categories.first { it.value.id == firstCategory.value.id }
            assertTrue(updatedFirstCategory.isSelected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickBack should send NavigateBack effect when called`() = runTest(testDispatcher) {
        viewModel.onClickBack()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is UpdateCategoriesScreenEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickSave should sendEffect NavigateToTrends when patchUserCategories is successful`() =
        runTest(testDispatcher) {
            everySuspend { repository.updateUserCategories(any(), any()) } returns Unit

            viewModel.onClickSave()

            viewModel.effect.test {
                val effect = awaitItem()
                assertTrue(effect is UpdateCategoriesScreenEffect.NavigateToTrendsAndShowSuccess)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickSave should set errorState when patchUserCategories throws exception`() =
        runTest(testDispatcher) {
            everySuspend {
                repository.updateUserCategories(any(), any())
            } throws Exception()

            viewModel.onClickSave()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.errorState).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onRetryClick should reset error`() =
        runTest(testDispatcher) {
            viewModel.onClickRetry()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.errorState).isNull()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
