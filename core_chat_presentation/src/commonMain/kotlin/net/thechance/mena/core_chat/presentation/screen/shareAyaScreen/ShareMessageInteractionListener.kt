package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ShareMessageInteractionListener{
    fun onSearchQueryChanged(query: String)
    fun onContactClicked(contactId: Uuid?)
    fun onBackClicked()
    fun onClearQueryClicked()
}