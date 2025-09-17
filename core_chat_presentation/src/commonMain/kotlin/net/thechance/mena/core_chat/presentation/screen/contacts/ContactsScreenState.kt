package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.presentation.components.SnackBarData

data class ContactsScreenState(
    val contacts: Flow<PagingData<ContactUi>> = flowOf(PagingData.empty()),
    val isLastPage: Boolean = false,
    val snackBarData: SnackBarData? = null,
)
