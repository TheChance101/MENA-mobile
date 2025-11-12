package net.thechance.mena.identity.presentation.screen.contactUs

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)

class ContactUsViewModelTest {
    private val viewModel = ContactUsViewModel()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should navigate back to profile screen when click back button`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue { effect is ContactUsUIEffect.NavigateBack }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should open mail app when click on email address`() = runTest {
        viewModel.effect.test {
            viewModel.onClickEmailAddress()
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue { effect is ContactUsUIEffect.OpenUrl && isValidEmailUrl(effect.url) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should open dialer app when click on mobile number`() = runTest {
        viewModel.effect.test {
            viewModel.onClickPhoneNumber()
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue { effect is ContactUsUIEffect.OpenUrl && isValidPhoneUrl(effect.url) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should open browser app when click on facebook account`() = runTest {
        viewModel.effect.test {
            viewModel.onClickFacebookAccount()
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue { effect is ContactUsUIEffect.OpenUrl && isValidFacebookUrl(effect.url) }
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun isValidEmailUrl(url: String): Boolean = url.startsWith("mailto:")

    private fun isValidPhoneUrl(url: String): Boolean = url.startsWith("tel:")

    private fun isValidFacebookUrl(url: String): Boolean = url.startsWith("https://www.facebook.com")
}