package net.thechance.mena.core_chat.presentation.screen.syncContacts

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel

class SyncContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val permissionsController: PermissionsController
) : BaseViewModel<SyncContactsState, SyncContactsScreenEffect>(SyncContactsState()),
    SyncContactsScreenInteractionListener {


    override fun onForceSync(forceSync: Boolean) {
        if (forceSync) {
            updateState { it.copy(isFirstSync = false) }
            syncContacts()
        } else {
            updateState { it.copy(isFirstSync = true, showSyncView = true, isLoading = false) }
        }
    }

    override fun onBackClick() {
        emitEffect(SyncContactsScreenEffect.NavigateBack)
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
                updateState { it.copy(
                    isLoading = false,
                    deniedPermanently = true,
                    snackBarData = SnackBarData(
                        title = "Permission denied",
                        message = "Contacts permission is required to sync contacts",
                    )
                ) }
            }
            is DeniedException -> {
                updateState {
                    it.copy(
                        isLoading = false,
                        snackBarData = SnackBarData(
                            title = "Permission denied",
                            message = "Contacts permission is required to sync contacts",
                        )
                    )
                }
            }
            else -> onError(throwable)
        }
    }

    override fun onSnackBarDismiss() {
        updateState { it.copy(snackBarData = null) }
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
            emitEffect(SyncContactsScreenEffect.NavigateToContacts)
        } else {
            emitEffect(SyncContactsScreenEffect.NavigateBackWithResult)
        }
    }

    private fun onError(throwable: Throwable) {
        updateState {
            it.copy(
                isLoading = false,
                snackBarData = SnackBarData(
                    title = "Something went wrong",
                    message = throwable.message ?: "Unknown error",
                )
            )
        }
    }
}

