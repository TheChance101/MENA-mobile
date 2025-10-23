package net.thechance.mena.core_chat.presentation.screen.syncContacts

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contacts_permission_required_message
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.utils.SettingsOpener
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SyncContactsViewModelTest {

    private val contactsRepository = mock<ContactsRepository>()
    private val permissionsController = mock<PermissionsController>()
    private val settingsOpener = mock<SettingsOpener>()

    private val testDispatcher = StandardTestDispatcher()

    private fun createSyncContactsScreenArgs(forceSyncParam: Boolean): SyncContactsScreenArgs {
        return mock {
            every { forceSync } returns forceSyncParam
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `init should set isFirstSync to false and call syncContacts when forceSync is true`() =
        runTest {
            everySuspend { contactsRepository.syncContacts() } returns Unit
            everySuspend { contactsRepository.setHasUserSyncedContactsStatus(true) } returns Unit

            val viewModel = createSyncContactsViewModel(true)
            advanceUntilIdle()
            val result = viewModel.state.first()

            assertThat(result.isFirstSync).isFalse()
            verifySuspend { contactsRepository.syncContacts() }
        }

    @Test
    fun `init should set isFirstSync to true and showSyncView when forceSync is false`() = runTest {

        val viewModel = createSyncContactsViewModel(false)

        advanceUntilIdle()
        val result = viewModel.state.first()

        assertThat(result.isFirstSync).isTrue()
        assertThat(result.showSyncView).isTrue()
    }

    @Test
    fun `onSyncClicked should request permission and sync contacts when called successfully`() =
        runTest {
            everySuspend { permissionsController.providePermission(Permission.CONTACTS) } returns Unit
            everySuspend { contactsRepository.syncContacts() } returns Unit
            everySuspend { contactsRepository.setHasUserSyncedContactsStatus(true) } returns Unit

            val viewModel = createSyncContactsViewModel(true)
            viewModel.state.test {
                awaitItem()
                viewModel.onSyncClicked()
                awaitItem()

                verifySuspend { permissionsController.providePermission(Permission.CONTACTS) }
                verifySuspend { contactsRepository.syncContacts() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onSyncClicked should emit SnackBar effect when permission is denied`() = runTest {
        everySuspend { permissionsController.providePermission(Permission.CONTACTS) } throws DeniedException(
            Permission.CONTACTS
        )
        val viewModel = createSyncContactsViewModel(false)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onSyncClicked()
            advanceUntilIdle()

            assertEquals(
                SyncContactsScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.permission_denied_title),
                        message = UiText.StringRes(Res.string.contacts_permission_required_message),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSyncClicked should set isPermissionDeniedPermanently to true when providePermission throw DeniedAlwaysException`() =
        runTest {
            everySuspend { permissionsController.providePermission(Permission.CONTACTS) } throws DeniedAlwaysException(
                Permission.CONTACTS
            )

            val viewModel = createSyncContactsViewModel(false)

            viewModel.state.test {
                awaitItem()

                viewModel.onSyncClicked()

                val state = awaitItem()
                assertThat(state.isPermissionDeniedPermanently).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `init should emit snackBar effect when syncContacts throw exception`() = runTest {
        everySuspend { contactsRepository.syncContacts() } throws Exception()

        val viewModel = createSyncContactsViewModel(true)

        viewModel.effect.test {
            advanceUntilIdle()

            assertEquals(
                SyncContactsScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.something_went_wrong),
                        message = UiText.StringRes(Res.string.could_not_sync_contacts_message),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onBackClicked should emit NavigateBack effect when called`() = runTest {
        everySuspend { contactsRepository.syncContacts() } returns Unit
        everySuspend { contactsRepository.setHasUserSyncedContactsStatus(true) } returns Unit
        val viewModel = createSyncContactsViewModel(true)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onBackClicked()
            advanceUntilIdle()

            assertEquals(SyncContactsScreenEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `checkPermissions should update state and call syncContacts when permission is granted`() =
        runTest {
            everySuspend { permissionsController.isPermissionGranted(Permission.CONTACTS) } returns true
            everySuspend { contactsRepository.syncContacts() } returns Unit

            val viewModel = createSyncContactsViewModel(false)
            advanceUntilIdle()

            viewModel.state.test {
                awaitItem()
                viewModel.checkPermissions()
                val state = awaitItem()
                assertThat(state.isPermissionDeniedPermanently).isFalse()
                verifySuspend { contactsRepository.syncContacts() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onGoToSettingsClicked should call openSettings when called`() = runTest {
        everySuspend { contactsRepository.syncContacts() } returns Unit
        everySuspend { contactsRepository.setHasUserSyncedContactsStatus(true) } returns Unit
        everySuspend { settingsOpener.openSettings() } returns Unit

        val viewModel = createSyncContactsViewModel(false)
        advanceUntilIdle()
        viewModel.onGoToSettingsClicked()

        verifySuspend { settingsOpener.openSettings() }
    }

    private fun createSyncContactsViewModel(
        forceSyncParam: Boolean
    ): SyncContactsViewModel {
        return SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            createSyncContactsScreenArgs(forceSyncParam),
            settingsOpener,
            testDispatcher
        )
    }
}