package net.thechance.mena.identity.presentation.screen.privacyAndPolicy

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy
import net.thechance.mena.identity.domain.model.Section
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class PrivacyAndPolicyScreenViewModelTest : BaseCoroutineTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val applicationInfoRepository = mockk<ApplicationInfoRepository>()

    val viewModel: PrivacyAndPolicyScreenViewModel by lazy {
        PrivacyAndPolicyScreenViewModel(
            applicationInfoRepository = applicationInfoRepository,
            dispatcher = testDispatcher
        )
    }

    @Before
    override fun setUp() {
        super.setUp()
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
    fun `getPrivacyAndPolicy() should update state when get privacy and policy successfully`() =
        runTest {
            coEvery { applicationInfoRepository.getPrivacyAndPolicy() } returns fakePrivacyAndPolicy

            viewModel
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().privacyAndPolicySections).isNotEmpty()
            }
        }

    @Test
    fun `getPrivacyAndPolicy() should show snack bar with error message when get privacy and policy throws exception`() =
        runTest {
            coEvery { applicationInfoRepository.getPrivacyAndPolicy() } throws UnAuthorizedException()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(PrivacyAndPolicyScreenUIEffect.ShowSnackBarError::class)
            }
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