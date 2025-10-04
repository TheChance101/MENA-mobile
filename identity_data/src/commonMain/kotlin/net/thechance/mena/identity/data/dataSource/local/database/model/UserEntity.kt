package net.thechance.mena.identity.data.dataSource.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val username: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
)