package net.thechance.mena.trends.presentation.screen.category_pick

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.screen.util.MainDispatcherTest
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable
import org.koin.test.KoinTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPickViewModelTest : MainDispatcherTest(), KoinTest {

    private val repository = mock<CategoryRepository>() {
        everySuspend { getAllCategories() } returns categories
    }
    private val viewModel by lazy { CategoryPickViewModel(repository) }

    @Test
    fun `init should get all categories from repository`() = runTest {
        viewModel

        viewModel.state.test {
            assertThat(awaitItem().categories).isEqualTo(categoriesUiStates)
        }
    }

    @Test
    fun `state categories should be empty when when repository returns empty list`() = runTest {
        everySuspend { repository.getAllCategories() } returns emptyList()

        viewModel

        viewModel.state.test {
            assertThat(awaitItem().categories).isEmpty()
        }
    }

    @Test
    fun `screen loading state should be updated to false when viewmodel initialized`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem().isLoading).isFalse()
        }
    }

    @Test
    fun `isNextButtonLoading should be false if no category is selected`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem().isNextButtonLoading).isFalse()
        }
    }

    @Test
    fun `onCategoryClick should toggle selected category when category clicked`() = runTest {
        viewModel.onCategoryClick(categoriesUiStates.first().value.id!!)

        viewModel.state.test {
            assertThat(awaitItem().categories.first().isSelected).isTrue()
        }
    }

    @Test
    fun `onNextClick should send NavigateToTrends effect when success`() = runTest {
        everySuspend { repository.updateUserInterestedCategories(any()) } returns Unit

        viewModel.effect.test {
            viewModel.onNextClick()
            assertThat(awaitItem()).isInstanceOf<CategoryPickScreenEffect.NavigateToTrends>()
        }
    }

    @Test
    fun `onBackClick should send NavigateBack effect when clicked`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClick()
            assertThat(awaitItem()).isInstanceOf<CategoryPickScreenEffect.NavigateBack>()
        }
    }

    private companion object {
        val categories = listOf(
            Category("uuid 1", "laugh", "😂"),
            Category("uuid 2", "music", "😂")
        )
        val categoriesUiStates: List<Selectable<CategoryUiState>> = categories.toUiStates()
    }
}