package net.thechance.mena.core_chat.presentation.screen.syncContacts

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contacts_permission_required_message
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.something_went_wrong
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactsScreenArgs.Companion.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.openAppSettings

class SyncContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val permissionsController: PermissionsController,
    private val syncContactsScreenArgs: SyncContactsScreenArgs,
    effector: ChatEffector
) : BaseViewModel<SyncContactsScreenState>(SyncContactsScreenState(), effector),
    SyncContactsInteractionListener {

    init {
        onForceSync()
    }

    fun onForceSync() {
        if (syncContactsScreenArgs.forceSync) {
            updateState { it.copy(isFirstSync = false) }
            syncContacts()
        } else {
            updateState { it.copy(isFirstSync = true, showSyncView = true, isLoading = false) }
        }
    }

    override fun onBackClick() {
        popBackStack()
    }

    override fun onSyncClick() {
        tryToExecute(
            execute = { permissionsController.providePermission(Permission.CONTACTS) },
            onSuccess = { syncContacts() },
            onError = ::handlePermissionError
        )
    }

    private fun handlePermissionError(throwable: Throwable) {
        when (throwable) {
            is DeniedAlwaysException -> {
                updateState {
                    it.copy(
                        isLoading = false,
                        deniedPermanently = true,
                    )
                }
                showSnackBar(
                    snackBarData = SnackBarData(
                        title = Res.string.permission_denied_title,
                        message = Res.string.contacts_permission_required_message,
                    )
                )
            }

            is DeniedException -> {
                updateState { it.copy(isLoading = false) }
                showSnackBar(
                    SnackBarData(
                        title = Res.string.permission_denied_title,
                        message = Res.string.contacts_permission_required_message,
                    )
                )
            }

            else -> onError(throwable)
        }
    }

    private fun onContactsPermissionGranted() {
        updateState {
            it.copy(
                deniedPermanently = false,
                showSyncView = true
            )
        }
        syncContacts()
    }

    fun checkPermissions() {
        tryToExecute(
            execute = { permissionsController.isPermissionGranted(Permission.CONTACTS) },
            onSuccess = { granted ->
                if (granted) {
                    onContactsPermissionGranted()
                }
            }
        )
    }

    override fun onGoToSettingsClick() {
        openAppSettings()
    }


    private fun syncContacts() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoading = true, showSyncView = true) } },
            execute = { contactsRepository.syncContacts() },
            onSuccess = { onSyncContactsSuccess() },
            onError = ::onError
        )
    }

    private suspend fun onSyncContactsSuccess() {
        if (state.value.isFirstSync) {
            contactsRepository.setUserSyncedState(true)
            popBackStack()
            navigate(ContactsRoute)
        } else {
            popBackStack(IS_SYNC_SUCCESS to true)
        }
    }

    private fun onError(throwable: Throwable) {
        updateState {
            it.copy(
                isLoading = false,
            )
        }
        showSnackBar(
            SnackBarData(
                title = Res.string.something_went_wrong,
                message = Res.string.could_not_sync_contacts_message,
            )
        )
    }
}

