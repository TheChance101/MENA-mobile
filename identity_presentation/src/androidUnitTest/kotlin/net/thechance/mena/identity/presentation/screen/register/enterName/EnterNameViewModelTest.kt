package net.thechance.mena.identity.presentation.screen.register.enterName

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class EnterNameViewModelTest : BaseCoroutineTest() {
    private lateinit var enterNameViewModel: EnterNameViewModel
    private val registerRepository = mockk<RegisterRepository>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        enterNameViewModel = EnterNameViewModel(
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            phoneNumber = PhoneNumber("+964", "7901234567"),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onChangeFirstName should update firstName in state`() {
        val firstName = "Mohammed"

        enterNameViewModel.onChangeFirstName(firstName)

        assert(enterNameViewModel.state.value.firstName == firstName)
    }

    @Test
    fun `onChangeFirstName should enable next button when all fields are filled`() {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")

        assert(enterNameViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onLastNameChange should update lastName in state`() {
        val lastName = "Ahmed"

        enterNameViewModel.onLastNameChange(lastName)

        assert(enterNameViewModel.state.value.lastName == lastName)
    }

    @Test
    fun `onLastNameChange should enable next button when all fields are filled`() {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")

        assert(enterNameViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onUsernameChange should update username in state`() {
        val username = "mohammed123"

        enterNameViewModel.onUsernameChange(username)

        assert(enterNameViewModel.state.value.username == username)
    }

    @Test
    fun `onUsernameChange should clear username error`() {
        enterNameViewModel.onUsernameChange("mohammed123")

        assert(enterNameViewModel.state.value.usernameError == null)
    }

    @Test
    fun `onUsernameChange should enable next button when all fields are filled`() {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")

        assert(enterNameViewModel.state.value.isNextEnabled)
    }

    @Test
    fun `onClickNext should set isLoading to true`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.onClickNext()

        assert(enterNameViewModel.state.value.isLoading)
    }

    @Test
    fun `onClickNext should set isCheckingUsername to true`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.onClickNext()

        assert(enterNameViewModel.state.value.isCheckingUsername)
    }

    @Test
    fun `onClickNext should navigate to password screen on success`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.effect.test {
            enterNameViewModel.onClickNext()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assert(effect is EnterNameUIEffect.NavigateToPassword)
        }
    }

    @Test
    fun `onClickNext should pass correct firstName in navigation effect`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.effect.test {
            enterNameViewModel.onClickNext()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem() as EnterNameUIEffect.NavigateToPassword
            assert(effect.registerUIState.firstName == "Mohammed")
        }
    }

    @Test
    fun `onClickNext should pass correct lastName in navigation effect`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.effect.test {
            enterNameViewModel.onClickNext()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem() as EnterNameUIEffect.NavigateToPassword
            assert(effect.registerUIState.lastName == "Ahmed")
        }
    }

    @Test
    fun `onClickNext should pass correct username in navigation effect`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.effect.test {
            enterNameViewModel.onClickNext()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem() as EnterNameUIEffect.NavigateToPassword
            assert(effect.registerUIState.username == "mohammed123")
        }
    }

    @Test
    fun `onClickNext should set isLoading to false on success`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.onClickNext()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!enterNameViewModel.state.value.isLoading)
    }

    @Test
    fun `onClickNext should set isCheckingUsername to false on success`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } returns true

        enterNameViewModel.onClickNext()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!enterNameViewModel.state.value.isCheckingUsername)
    }

    @Test
    fun `onClickNext should set isLoading to false on error`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } throws Exception("Test error")

        enterNameViewModel.onClickNext()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!enterNameViewModel.state.value.isLoading)
    }

    @Test
    fun `onClickNext should set isCheckingUsername to false on error`() = runTest {
        enterNameViewModel.onChangeFirstName("Mohammed")
        enterNameViewModel.onLastNameChange("Ahmed")
        enterNameViewModel.onUsernameChange("mohammed123")
        coEvery { registerRepository.checkUserExistence(any()) } throws Exception("Test error")

        enterNameViewModel.onClickNext()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(!enterNameViewModel.state.value.isCheckingUsername)
    }

    @Test
    fun `loadSavedData should load saved draft data`() = runTest {
        val savedDraft = net.thechance.mena.identity.domain.model.RegistrationDraft(
            firstName = "SavedFirstName",
            lastName = "SavedLastName",
            username = "savedusername"
        )
        coEvery { registerRepository.checkUserExistence(any()) } returns true
        coEvery { registrationDraftRepository.getDraft(any()) } returns savedDraft

        val viewModel = EnterNameViewModel(
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            phoneNumber = PhoneNumber("+964", "7901234567"),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("SavedFirstName", viewModel.state.value.firstName)
    }

    @Test
    fun `onChangeFirstName should save draft`() = runTest {
        coEvery { registrationDraftRepository.getDraft(any()) } returns net.thechance.mena.identity.domain.model.RegistrationDraft()
        coEvery { registrationDraftRepository.saveDraft(any(), any()) } returns Unit

        enterNameViewModel.onChangeFirstName("NewFirstName")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            registrationDraftRepository.saveDraft(
                any(),
                net.thechance.mena.identity.domain.model.RegistrationDraft(firstName = "NewFirstName")
            )
        }
    }

    @Test
    fun `onLastNameChange should save draft`() = runTest {
        coEvery { registrationDraftRepository.getDraft(any()) } returns net.thechance.mena.identity.domain.model.RegistrationDraft()
        coEvery { registrationDraftRepository.saveDraft(any(), any()) } returns Unit

        enterNameViewModel.onLastNameChange("NewLastName")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            registrationDraftRepository.saveDraft(
                any(),
                net.thechance.mena.identity.domain.model.RegistrationDraft(lastName = "NewLastName")
            )
        }
    }

    @Test
    fun `onUsernameChange should save draft`() = runTest {
        coEvery { registrationDraftRepository.getDraft(any()) } returns net.thechance.mena.identity.domain.model.RegistrationDraft()
        coEvery { registrationDraftRepository.saveDraft(any(), any()) } returns Unit

        enterNameViewModel.onUsernameChange("newusername")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            registrationDraftRepository.saveDraft(
                any(),
                net.thechance.mena.identity.domain.model.RegistrationDraft(username = "newusername")
            )
        }
    }
}