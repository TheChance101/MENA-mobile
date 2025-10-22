package net.thechance.mena.trends.presentation.screen.category_pick

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
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.utils.TestExtensions
import net.thechance.mena.trends.presentation.utils.categories
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryPickViewModelTest : TestExtensions() {
    private val repository: CategoryRepository = mock<CategoryRepository>(mode = MockMode.autofill)
    private val viewModel by lazy {
        CategoryPickViewModel(
            repository = repository,
            defaultDispatcher = testDispatcher
        )
    }

    @BeforeTest
    fun setup() {
        everySuspend { repository.getAllCategories() } returns categories
    }

    @Test
    fun `loadCategories should start loading when initially called`() =
        runTest(testDispatcher) {
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.isLoading)
            }
        }

    @Test
    fun `loadCategories should return categories with success when repository returns value`() =
        runTest(testDispatcher) {
            viewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertEquals(categories.size, state.categories.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadCategories should throw exception when initially called`() =
        runTest(testDispatcher) {
            everySuspend { repository.getAllCategories() } throws Exception()

            viewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertThat(state.error).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadCategories should end loading when initially called`() =
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
            assertTrue(effect is CategoryPickScreenEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickNext should called updateUserInterestedCategories from repository with success`() =
        runTest(testDispatcher) {
            val selectedIds = listOf(categories.first().id)
            everySuspend { repository.initializeUserCategories(selectedIds) } returns Unit

            viewModel.onClickNext()

            viewModel.effect.test {
                val effect = awaitItem()
                assertTrue(effect is CategoryPickScreenEffect.NavigateToHome)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickNext should throw exception when called`() =
        runTest(testDispatcher) {
            everySuspend {
                repository.initializeUserCategories(any())
            } throws Exception()

            viewModel.onClickNext()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.error).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onRetryClick should reset error and call getAllCategories`() = runTest {
        viewModel.onClickRetry()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            verifySuspend { repository.getAllCategories() }
            cancelAndIgnoreRemainingEvents()
        }
    }
}