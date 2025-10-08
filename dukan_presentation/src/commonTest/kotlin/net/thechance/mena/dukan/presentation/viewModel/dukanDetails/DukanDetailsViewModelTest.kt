package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dukanDetailsViewModel: DukanDetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dukanDetailsViewModel = DukanDetailsViewModel()
    }

    @Test
    fun `when onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        dukanDetailsViewModel.onBackClicked()
        advanceUntilIdle()
        dukanDetailsViewModel.effect.test {
            val effect = awaitItem()
            assertEquals(DukanDetailsEffects.NavigateBack, effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when onViewAllShelfProductsClicked SHOULD emit NavigateToViewAllShelfProducts effect`() = runTest {
        dukanDetailsViewModel.onViewAllShelfProductsClicked("id123", "shelf")
        advanceUntilIdle()
        dukanDetailsViewModel.effect.test {
            val effect = awaitItem()
            assertEquals(DukanDetailsEffects.NavigateToViewAllShelfProducts("id123", "shelf"), effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when onViewDukanOnMapClicked SHOULD emit NavigateToViewDukanOnMap effect`() = runTest {
        dukanDetailsViewModel.onViewDukanOnMapClicked(28.0, 29.0)
        advanceUntilIdle()
        dukanDetailsViewModel.effect.test {
            val effect = awaitItem()
            assertEquals(DukanDetailsEffects.NavigateToViewDukanOnMap(28.0, 29.0), effect)
            cancelAndIgnoreRemainingEvents()
        }
    }
}