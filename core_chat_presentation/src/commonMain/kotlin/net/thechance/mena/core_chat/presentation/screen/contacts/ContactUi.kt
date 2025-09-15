package net.thechance.mena.core_chat.presentation.screen.contacts


data class ContactUi(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String,
    val isMenaMember: Boolean,
    val imageUri: String? = null
) {
    val displayName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            !lastName.isNullOrBlank() -> lastName
            else -> "Unknown"
        }

    val initials: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() ->
                "${firstName.firstOrNull()} ${lastName.firstOrNull()}".uppercase()
            !firstName.isNullOrBlank() ->
                firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            !lastName.isNullOrBlank() ->
                lastName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            else -> "?"
        }
}

val temporaryContacts = listOf(
    ContactUi(
        id = "1",
        firstName = "John",
        lastName = "Doe",
        phoneNumber = "+1234567890",
        isMenaMember = true,
        imageUri = "https://fastly.picsum.photos/id/834/200/200.jpg?hmac=vcoSQ7O6i2vxWANscm-9EGrw0MNqLzU3X0pQZ1o5ovI"
    ),
    ContactUi(
        id = "2",
        firstName = "Noor",
        lastName = "Al-Taie",
        phoneNumber = "+1987654321",
        isMenaMember = false,
        imageUri = null
    ),
    ContactUi(
        id = "3",
        firstName = "Alice",
        lastName = "Johnson",
        phoneNumber = "+1122334455",
        isMenaMember = true,
        imageUri = "https://fastly.picsum.photos/id/310/200/200.jpg?hmac=gpEKQ-zUG9L-jZga6K0jQ2NHHqqoPzMKUR-_ZmiL734"
    ),
    ContactUi(
        id = "4",
        firstName = "Bob",
        lastName = "Brown",
        phoneNumber = "+1222333444",
        isMenaMember = false,
        imageUri = null
    ),
    ContactUi(
        id = "5",
        firstName = null,
        lastName = null,
        phoneNumber = "+1555666777",
        isMenaMember = false,
        imageUri = "https://fastly.picsum.photos/id/57/200/200.jpg?hmac=EAluVy04ceTUijEPw3vraS5dkJ6vtBD3HmNwvMI5f3k"
    )
)