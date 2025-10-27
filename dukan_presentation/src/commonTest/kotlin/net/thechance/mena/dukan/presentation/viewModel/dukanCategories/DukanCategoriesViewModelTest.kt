package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DukanCategoriesViewModelTest {

    private val dukanManagementRepository = mock<DukanManagementRepository>()
    lateinit var viewModel: DukanCategoriesViewModel

    val testDispatcher = StandardTestDispatcher()
    private val scope = TestScope(testDispatcher)

    @BeforeTest
    fun setup() {
        viewModel = DukanCategoriesViewModel(
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @Test
    fun `init with default dispatcher`() = scope.runTest {
        val viewModel = DukanCategoriesViewModel(
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
        assertNotNull(viewModel)
    }


    @Test
    fun `initScope - get all dukan categories`() = scope.runTest {

        everySuspend { dukanManagementRepository.getCategories() } returns dummyCategories

        viewModel = DukanCategoriesViewModel(
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
        advanceUntilIdle()

        viewModel.state.test {
            val firstEmit = awaitItem()
            assertEquals(
                expected = dummyCategories.map { it.toUiState() },
                actual = firstEmit.categories
            )
        }
    }

    @Test
    fun `initScope - update error message when repository throws NoInternetException`() =
        scope.runTest {

            everySuspend { dukanManagementRepository.getCategories() } throws NoInternetException()

            viewModel = DukanCategoriesViewModel(
                dukanManagementRepository = dukanManagementRepository,
                defaultDispatcher = testDispatcher
            )
            advanceUntilIdle()

            viewModel.state.test {
                val firstEmit = awaitItem()
                assertEquals(
                    expected = DukanCategoriesUiState.DukanCategoriesState.ERROR,
                    actual = firstEmit.dukanCategoriesState
                )
            }
        }

    @Test
    fun `initScope - update error message when repository throws DukanException`() = scope.runTest {

        everySuspend { dukanManagementRepository.getCategories() } throws DukanException()

        viewModel = DukanCategoriesViewModel(
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
        advanceUntilIdle()

        viewModel.state.test {
            val firstEmit = awaitItem()
            assertEquals(
                expected = Res.string.something_went_wrong,
                actual = firstEmit.snackBarUiState!!.message
            )
        }
    }

    @Test
    fun `onBackClicked - emit NavigateBack effect `() = scope.runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            val currentEffect = awaitItem()
            assertEquals(
                expected = DukanCategoriesEffects.NavigateBack,
                actual = currentEffect
            )
        }
    }

    @Test
    fun `onCategoryClicked - emit NavigateToDukanCategoryDetails effect `() = scope.runTest {
        val fakeCategoryName = "Electronics"
        val fakeCategoryId = "14"

        viewModel.effect.test {
            viewModel.onCategoryClicked(fakeCategoryName, fakeCategoryId)
            val currentEffect = awaitItem()
            assertEquals(
                expected = DukanCategoriesEffects.NavigateToDukansOfCategory(
                    fakeCategoryName,
                    fakeCategoryId
                ),
                actual = currentEffect
            )
        }
    }

    @Test
    fun `onDismissSnackBar - update snackBarUiState to null`() = scope.runTest {
        everySuspend { dukanManagementRepository.getCategories() } throws DukanException()

        viewModel = DukanCategoriesViewModel(
            dukanManagementRepository = dukanManagementRepository,
            defaultDispatcher = testDispatcher
        )
        advanceUntilIdle()

        viewModel.state.test {
            skipItems(1)
            viewModel.onDismissSnackBar()
            val currentState = awaitItem()
            assertNull(currentState.snackBarUiState)
        }
    }
}