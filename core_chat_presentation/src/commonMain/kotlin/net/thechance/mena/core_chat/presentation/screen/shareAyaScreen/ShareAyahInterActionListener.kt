package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ShareAyahInterActionListener{
    fun onChangeSearchQuery(query: String)
    fun onClickContact(contactId: Uuid?)
    fun onClickBack()
    fun onClickClearQuery()
}