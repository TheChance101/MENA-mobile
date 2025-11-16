package net.thechance.mena.identity.presentation.screen.privacyAndPolicy

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy
import net.thechance.mena.identity.domain.model.Section
import net.thechance.mena.identity.domain.repository.PrivacyAndPolicyRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrivacyAndPolicyScreenViewModelTest : BaseCoroutineTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val privacyRepository = mockk<PrivacyAndPolicyRepository>()
    private lateinit var viewModel: PrivacyAndPolicyScreenViewModel


    @Before
    override fun setUp() {
        super.setUp()
        viewModel = PrivacyAndPolicyScreenViewModel(
            policyRepository = privacyRepository,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onBackButtonClicked should emit NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            val effect = awaitItem()
            assertTrue(effect is PrivacyAndPolicyScreenUIEffect.NavigateBack)
        }
    }

    @Test
    fun `onClearErrorMessage() should update errorMessage to null`() = runTest {
        viewModel.onClearErrorMessage()
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `getPrivacyAndPolicy() should update state when get privacy and policy successfully`() =
        runTest {
            coEvery { privacyRepository.getPrivacyAndPolicy() } returns fakePrivacyAndPolicy
            testDispatcher.scheduler.advanceUntilIdle()
            assert(viewModel.state.value.privacyAndPolicySections.isNotEmpty())
        }

    @Test
    fun `getPrivacyAndPolicy() should update error message when get privacy and policy throws exception`() =
        runTest {
            coEvery { privacyRepository.getPrivacyAndPolicy() } throws UnAuthorizedException()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.state.value
            assertTrue { state.errorMessage != null }
        }


    val fakePrivacyAndPolicy = PrivacyAndPolicy(
        updateDate = LocalDate(2025, 12, 1),
        sections = listOf(
            Section(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. " +
                        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                        " when an unknown printer took a galley of type and scrambled it to make a type specimen book." +
                        " It has survived not only five centuries"
            )

        )

    )
}