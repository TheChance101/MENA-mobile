package net.thechance.mena.core_chat.presentation.screen.syncContacts

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contacts_permission_required_message
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.SettingsOpener
import net.thechance.mena.core_chat.presentation.utils.UiText
import org.jetbrains.compose.resources.StringResource

class SyncContactsViewModel(
    private val contactsRepository: ContactsRepository,
    val permissionsController: PermissionsController,
    private val syncContactsScreenArgs: SyncContactsScreenArgs,
    private val settingsOpener: SettingsOpener,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<SyncContactsScreenState, SyncContactsScreenEffect>(
    SyncContactsScreenState(),
    dispatcher
),
    SyncContactsInteractionListener {

    init {
        checkForceSync()
    }

    private fun checkForceSync() {
        if (syncContactsScreenArgs.forceSync) {
            updateState { it.copy(isFirstSync = false) }
            syncContacts()
        } else {
            updateState { it.copy(isFirstSync = true, showSyncView = true, isLoading = false) }
        }
    }

    private fun syncContacts() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoading = true, showSyncView = true) } },
            execute = { contactsRepository.syncContacts() },
            onSuccess = { onSyncContactsSuccess() },
            onError = { onError() }
        )
    }

    private suspend fun onSyncContactsSuccess() {
        if (state.value.isFirstSync) {
            contactsRepository.setHasUserSyncedContactsStatus(true)
        }
        emitEffect(SyncContactsScreenEffect.NavigateToContactsAfterSyncSuccess)
    }

    override fun onSyncClicked() {
        tryToExecute(
            execute = { permissionsController.providePermission(Permission.CONTACTS) },
            onSuccess = { syncContacts() },
            onError = ::handlePermissionError
        )
    }

    private fun handlePermissionError(throwable: Throwable) {
        when (throwable) {
            is DeniedAlwaysException -> {
                updateState { it.copy(isLoading = false, isPermissionDeniedPermanently = true) }
                showSnackBar(
                    titleStringResource = Res.string.permission_denied_title,
                    messageStringResource = Res.string.contacts_permission_required_message,
                    isError = true
                )
            }

            is DeniedException -> {
                updateState { it.copy(isLoading = false) }
                showSnackBar(
                    titleStringResource = Res.string.permission_denied_title,
                    messageStringResource = Res.string.contacts_permission_required_message,
                    isError = true
                )
            }

            else -> onError()
        }
    }

    private fun onContactsPermissionGranted() {
        updateState { it.copy(isPermissionDeniedPermanently = false, showSyncView = true) }
        syncContacts()
    }

    fun checkPermissions() {
        tryToExecute(
            onStart = { updateState { it.copy(isOpenSettingsCalled = false) } },
            execute = { permissionsController.isPermissionGranted(Permission.CONTACTS) },
            onSuccess = ::onPermissionsCheckSuccess
        )
    }

    private fun onPermissionsCheckSuccess(isGranted: Boolean) {
        if (isGranted) {
            onContactsPermissionGranted()
        }
    }

    override fun onGoToSettingsClicked() {
        settingsOpener.openSettings()
        updateState { it.copy(isOpenSettingsCalled = true) }
    }

    override fun onBackClicked() {
        emitEffect(SyncContactsScreenEffect.NavigateBack)
    }

    private fun onError() {
        updateState { it.copy(isLoading = false) }
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_sync_contacts_message,
            isError = true
        )
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean
    ) {
        emitEffect(
            SyncContactsScreenEffect.ShowSnackBar(
                SnackBarData(
                    title = UiText.StringRes(titleStringResource),
                    message = UiText.StringRes(messageStringResource),
                    isError = isError
                )
            )
        )
    }
}

