package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState

data class ShareMessageScreenState(
    val searchQuery: String = "",
    val contacts: Flow<PagingData<ContactUiState>> = flowOf(PagingData.empty()),
)