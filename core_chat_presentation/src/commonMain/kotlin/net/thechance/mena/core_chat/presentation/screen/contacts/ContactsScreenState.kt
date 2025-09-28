package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.domain.entity.Contact

data class ContactsScreenState(
    val contacts: Flow<PagingData<ContactUiState>> = flowOf(PagingData.empty())
)

data class ContactUiState(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isMenaUser: Boolean,
    val imageUri: String? = null
) {
    val displayName: String
        get() = when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            lastName.isNotBlank() -> lastName
            else -> "Unknown"
        }
}


fun Contact.toUiModel(): ContactUiState {
    return ContactUiState(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phone,
        isMenaUser = this.isMenaUser,
        imageUri = this.imageUrl
    )
}
