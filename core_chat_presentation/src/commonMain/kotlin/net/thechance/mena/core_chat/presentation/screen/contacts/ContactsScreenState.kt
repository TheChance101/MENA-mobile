package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.domain.entity.Contact
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ContactsScreenState(
    val contacts: Flow<PagingData<ContactUiState>> = flowOf(PagingData.empty())
)
@OptIn(ExperimentalUuidApi::class)
data class ContactUiState(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isMenaUser: Boolean,
    val menaUserId: Uuid?,
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