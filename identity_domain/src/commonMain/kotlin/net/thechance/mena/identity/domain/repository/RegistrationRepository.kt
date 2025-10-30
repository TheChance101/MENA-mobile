package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.User

interface RegistrationRepository {
    fun createUser(user: User, password: String)
    fun uploadImage(imageByteArray: ByteArray)
    fun isUsernameTaken(username: String): Boolean
}
