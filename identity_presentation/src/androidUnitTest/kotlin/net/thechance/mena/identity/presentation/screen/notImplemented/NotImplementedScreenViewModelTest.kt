package net.thechance.mena.identity.presentation.screen.notImplemented

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.helper.BaseCoroutineTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class NotImplementedScreenViewModelTest : BaseCoroutineTest() {

    private lateinit var viewModel: NotImplementedScreenViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = NotImplementedScreenViewModel()
    }

    @Test
    fun `onBackButtonClicked should emit NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onBackButtonClicked()

            val effect = awaitItem()
            assertTrue(effect is NotImplementedScreenUIEffect.NavigateBack)
        }
    }
}