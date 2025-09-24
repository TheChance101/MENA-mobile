package net.thechance.mena.core_chat.presentation.screen.contacts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactsScreenArgs(private val _isSyncSuccess: MutableStateFlow<Boolean>) {

   val isSyncSuccess: StateFlow<Boolean> = _isSyncSuccess.asStateFlow()

    fun setIsSyncSuccessToFalse(){
        _isSyncSuccess.value = false
    }
    companion object {
        const val IS_SYNC_SUCCESS = "is_sync_success"
    }
}