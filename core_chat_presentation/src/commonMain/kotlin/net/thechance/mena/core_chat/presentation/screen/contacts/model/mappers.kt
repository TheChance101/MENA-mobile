package net.thechance.mena.core_chat.presentation.screen.contacts.model

import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Contact.toUi(): ContactUiState {
    return ContactUiState(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phone,
        isMenaUser = menaUserId != null,
        menaUserId = this.menaUserId,
        imageUri = this.imageUrl
    )
}