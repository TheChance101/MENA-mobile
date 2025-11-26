package net.thechance.mena.identity.presentation.screen.contactUs

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.model.ContactInfo
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ContactUsViewModelTest {

    private val applicationInfoRepository: ApplicationInfoRepository = mockk(relaxed = true)
    private lateinit var viewModel: ContactUsViewModel
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val fakeContactInfo = ContactInfo(
        email = "test@email.com",
        phoneNumber = "123456789",
        facebookAccount = "https://www.facebook.com/test"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { applicationInfoRepository.getContactInfo() } returns fakeContactInfo
        viewModel = ContactUsViewModel(applicationInfoRepository, testDispatcher)
    }

    @Test
    fun `getContactInfo should update state with contact details on success`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.state.value
        assertFalse(currentState.isLoading)
        assertEquals(fakeContactInfo.email, currentState.email)
        assertEquals(fakeContactInfo.phoneNumber, currentState.phoneNumber)
        assertEquals(fakeContactInfo.facebookAccount, currentState.facebookUrl)
    }

    @Test
    fun `getContactInfo should send effect with error message on failure`() = runTest {
        coEvery { applicationInfoRepository.getContactInfo() } throws NoNetworkException()

        viewModel = ContactUsViewModel(applicationInfoRepository, testDispatcher)

        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(ContactUsUIEffect.ShowSnackBarError::class)
        }
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
        testDispatcher.scheduler.advanceUntilIdle()
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
        testDispatcher.scheduler.advanceUntilIdle()
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
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.effect.test {
            viewModel.onClickFacebookAccount()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue {
                effect is ContactUsUIEffect.OpenUrl &&
                isValidFacebookUrl(effect.url)
            }
        }
    }

    private fun isValidEmailUrl(url: String): Boolean = url.startsWith("mailto:")

    private fun isValidPhoneUrl(url: String): Boolean = url.startsWith("tel:")

    private fun isValidFacebookUrl(url: String): Boolean =
        url.startsWith("https://www.facebook.com")
}
