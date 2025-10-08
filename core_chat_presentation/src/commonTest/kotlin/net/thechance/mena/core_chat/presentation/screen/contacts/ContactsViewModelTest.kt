@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class, DelicateMokkeryApi::class)

package net.thechance.mena.core_chat.presentation.screen.contacts

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import dev.mokkery.annotations.DelicateMokkeryApi
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contact_not_mena_user
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.screen.syncContacts.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContactsViewModelTest {
    private val contactsRepository = mock<ContactsRepository>()

    private val chatRepository = mock<ChatRepository>()
    private val effector = mock<ChatEffector>()
    private val isSyncSuccessState: MutableStateFlow<Boolean> = MutableStateFlow(false)


    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { effector.setNavigationArgs(any()) } returns Unit
        val fakeFlow = MutableSharedFlow<Map<String, Any>>(replay = 1)
        every { effector.popBackStackArgsFlow } returns fakeFlow
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call load contacts when the user resync successfully`() = runTest {
        everySuspend { contactsRepository.getUserContacts(any()) } returns PagedData(
            data = listOf(expectedContact),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        isSyncSuccessState.update { true }

        viewModel.state.test {
            val state = awaitItem()
            val contacts = state.contacts.first()
            assertThat(contacts).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `init should load contacts successfully when viewModel is created`() = runTest {
        everySuspend { contactsRepository.getUserContacts(any()) } returns PagedData(
            data = listOf(expectedContact),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)

        viewModel.state.test {
            val state = awaitItem()
            val contacts = state.contacts.first()
            assertThat(contacts).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRefreshContactsClicked should load expected contact when repository returns data`() = runTest {
        everySuspend { contactsRepository.getUserContacts(any()) } returns PagedData(
            data = listOf(expectedContact),
            totalItems = 1,
            isLastPage = true
        )
        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        advanceUntilIdle()

        viewModel.onRefreshContactsClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.contacts).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRefreshContactsClicked should handle empty list when repository returns no contacts`() =
        runTest {
            everySuspend { contactsRepository.getUserContacts(any()) } returns PagedData(
                data = emptyList(),
                totalItems = 0,
                isLastPage = true
            )
            val viewModel =
                ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
            advanceUntilIdle()

            viewModel.onRefreshContactsClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val item = awaitItem()
                assertThat(item).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackClicked should call popBackStack when invoked`() = runTest {
        everySuspend { effector.popBackStack() } returns Unit
        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        advanceUntilIdle()

        viewModel.onBackClicked()
        advanceUntilIdle()

        verifySuspend {
            effector.popBackStack()
        }
    }

    @Test
    fun `onReSyncClicked should navigate to sync contacts when invoked`() = runTest {
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit
        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        advanceUntilIdle()

        viewModel.onReSyncClicked()
        advanceUntilIdle()

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `onContactClicked should navigate to chat screen when called`() = runTest {
        val expectedChat = Chat(
            id = Uuid.random(),
            imageUrl = null,
            name = "John Doe",
            requesterId = Uuid.random()
        )
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit
        everySuspend { chatRepository.getChatByContactUserId(any()) } returns expectedChat

        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        advanceUntilIdle()

        val contactId = Uuid.random()
        viewModel.onContactClicked(contactId)
        advanceUntilIdle()

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `onContactClicked should show snack bar when repository throws error`() = runTest {
        everySuspend { chatRepository.getChatByContactUserId(any()) } throws Exception()
        everySuspend { effector.showSnackBar(any()) } returns Unit

        val viewModel =
            ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
        advanceUntilIdle()

        viewModel.onContactClicked(Uuid.random())
        advanceUntilIdle()

        verifySuspend {
            effector.showSnackBar(matches {
                assertThat(it.message).isEqualTo(UiText.StringRes(Res.string.contact_not_mena_user))
                true
            })
        }
    }

    @Test
    fun `observeSyncSuccess should refresh contacts and reset flag when sync success is true`() =
        runTest {

            everySuspend { contactsRepository.getUserContacts(any()) } returns PagedData(
                data = listOf(expectedContact),
                totalItems = 1,
                isLastPage = true
            )
            val fakeFlow = MutableSharedFlow<Map<String, Any>>(replay = 1)
            every { effector.popBackStackArgsFlow } returns fakeFlow
            everySuspend { effector.setNavigationArgs(any()) } returns Unit

            val viewModel =
                ContactsViewModel(contactsRepository, chatRepository, effector, testDispatcher)
            advanceUntilIdle()

            fakeFlow.emit(mapOf(IS_SYNC_SUCCESS to true))
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.contacts).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }

            verifySuspend { effector.setNavigationArgs(IS_SYNC_SUCCESS to false) }
        }

    companion object {
        val expectedContact = Contact(
            firstName = "John",
            lastName = "Doe",
            phone = "123456789",
            imageUrl = null,
            menaUserId = Uuid.random()
        )
    }
}