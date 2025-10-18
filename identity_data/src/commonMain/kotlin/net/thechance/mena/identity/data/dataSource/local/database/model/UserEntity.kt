package net.thechance.mena.identity.data.dataSource.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
    val birthDate: String,
    val gender: Int,
) {
    companion object {
        const val MALE = 1
        const val FEMALE = 2
    }
}