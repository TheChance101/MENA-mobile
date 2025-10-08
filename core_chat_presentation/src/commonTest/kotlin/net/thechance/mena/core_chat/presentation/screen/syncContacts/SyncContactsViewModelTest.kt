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
import dev.mokkery.matcher.any
import dev.mokkery.matcher.varargs.anyVarargs
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.utils.SettingsOpener
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncContactsViewModelTest {

    private val contactsRepository = mock<ContactsRepository>()
    private val permissionsController = mock<PermissionsController>()
    private val settingsOpener = mock<SettingsOpener>()
    private val effector = mock<ChatEffector>()

    private val testDispatcher = StandardTestDispatcher()

    private fun createSyncContactsScreenArgs(forceSyncParam: Boolean): SyncContactsScreenArgs {
        return mock {
            every { forceSync } returns forceSyncParam
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fakeFlow = MutableSharedFlow<Map<String, Any>>(replay = 1)
        every { effector.popBackStackArgsFlow } returns fakeFlow
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `should set isFirstSync to false and call syncContacts when forceSync is true`() = runTest {
        everySuspend { contactsRepository.syncContacts() } returns Unit
        everySuspend { contactsRepository.setSyncStatus(true) } returns Unit
        everySuspend { effector.showSnackBar(any()) } returns Unit
        everySuspend { effector.popBackStack(any()) } returns Unit

        val syncContactsScreenArgs = createSyncContactsScreenArgs(true)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()
        val result = viewModel.state.first()

        assertThat(result.isFirstSync).isFalse()
        verifySuspend { contactsRepository.syncContacts() }
    }

    @Test
    fun `should set isFirstSync to true and showSyncView when forceSync is false`() = runTest {
        val syncContactsScreenArgs = createSyncContactsScreenArgs(false)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()
        val result = viewModel.state.first()

        assertThat(result.isFirstSync).isTrue()
        assertThat(result.showSyncView).isTrue()
        assertThat(result.isLoading).isFalse()
    }

    @Test
    fun `should request permission and sync contacts when onSyncClicked is called successfully`() =
        runTest {
            everySuspend { permissionsController.providePermission(Permission.CONTACTS) } returns Unit
            everySuspend { contactsRepository.syncContacts() } returns Unit
            everySuspend { contactsRepository.setSyncStatus(true) } returns Unit
            everySuspend { effector.showSnackBar(any()) } returns Unit
            everySuspend { effector.popBackStack() } returns Unit
            everySuspend { effector.navigate(any(), any(), any()) } returns Unit

            val syncContactsScreenArgs = createSyncContactsScreenArgs(false)

            val viewModel = SyncContactsViewModel(
                contactsRepository,
                permissionsController,
                syncContactsScreenArgs,
                settingsOpener,
                effector,
                testDispatcher
            )

            viewModel.state.test {
                awaitItem()

                viewModel.onSyncClicked()

                awaitItem()

                verifySuspend { permissionsController.providePermission(Permission.CONTACTS) }
                verifySuspend { contactsRepository.syncContacts() }
            }
        }

    @Test
    fun `onSyncClicked should show SnackBar when permission is denied`() = runTest {
        everySuspend { permissionsController.providePermission(Permission.CONTACTS) } throws DeniedException(
            Permission.CONTACTS
        )
        everySuspend { effector.showSnackBar(any()) } returns Unit
        val syncContactsScreenArgs = createSyncContactsScreenArgs(false)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()

        viewModel.onSyncClicked()
        advanceUntilIdle()

        verifySuspend { effector.showSnackBar(any()) }
    }

    @Test
    fun `should handle DeniedAlwaysException when permission is permanently denied`() = runTest {
        everySuspend { permissionsController.providePermission(Permission.CONTACTS) } throws DeniedAlwaysException(
            Permission.CONTACTS
        )
        everySuspend { effector.showSnackBar(any()) } returns Unit

        val syncContactsScreenArgs = createSyncContactsScreenArgs(false)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )

        viewModel.state.test {
            awaitItem()

            viewModel.onSyncClicked()

            val error = awaitItem()
            assertThat(error.isLoading).isFalse()
            assertThat(error.isPermissionDeniedPermanently).isTrue()

            verifySuspend { permissionsController.providePermission(Permission.CONTACTS) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle sync error with proper error message`() = runTest {
        val errorMessage = "Sync failed"
        everySuspend { contactsRepository.syncContacts() } throws Exception(errorMessage)
        everySuspend { effector.showSnackBar(any()) } returns Unit

        val syncContactsScreenArgs = createSyncContactsScreenArgs(true)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()

        viewModel.state.test {
            verifySuspend { effector.showSnackBar(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onBackClicked should pop back stack when called`() = runTest {
        everySuspend { contactsRepository.syncContacts() } returns Unit
        everySuspend { contactsRepository.setSyncStatus(true) } returns Unit
        everySuspend { effector.showSnackBar(any()) } returns Unit
        everySuspend { effector.popBackStack(*anyVarargs()) } returns Unit

        val syncContactsScreenArgs = createSyncContactsScreenArgs(true)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()

        viewModel.onBackClicked()
        advanceUntilIdle()

        verifySuspend { effector.popBackStack() }
    }

    @Test
    fun `checkPermissions should update state and call syncContacts when permission is granted`() =
        runTest {
            everySuspend { permissionsController.isPermissionGranted(Permission.CONTACTS) } returns true
            everySuspend { contactsRepository.syncContacts() } returns Unit
            everySuspend { effector.showSnackBar(any()) } returns Unit
            val syncContactsScreenArgs = createSyncContactsScreenArgs(false)
            val viewModel = SyncContactsViewModel(
                contactsRepository,
                permissionsController,
                syncContactsScreenArgs,
                settingsOpener,
                effector,
                testDispatcher
            )
            advanceUntilIdle()

            viewModel.state.test {
                awaitItem()
                viewModel.checkPermissions()
                val state = awaitItem()
                assertThat(state.isPermissionDeniedPermanently).isFalse()
                assertThat(state.showSyncView).isTrue()
                verifySuspend { contactsRepository.syncContacts() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onGoToSettingsClicked should call openAppSettings when called`() = runTest {
        everySuspend { contactsRepository.syncContacts() } returns Unit
        everySuspend { contactsRepository.setSyncStatus(true) } returns Unit
        everySuspend { effector.showSnackBar(any()) } returns Unit
        everySuspend { settingsOpener.openSettings() } returns Unit

        val syncContactsScreenArgs = createSyncContactsScreenArgs(false)
        val viewModel = SyncContactsViewModel(
            contactsRepository,
            permissionsController,
            syncContactsScreenArgs,
            settingsOpener,
            effector,
            testDispatcher
        )
        advanceUntilIdle()
        viewModel.onGoToSettingsClicked()

        verifySuspend { settingsOpener.openSettings() }
    }
}
