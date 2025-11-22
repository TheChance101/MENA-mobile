package net.thechance.mena.identity.data.dataSource.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Address")
data class AddressEntity(

    @PrimaryKey
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val isActive: Boolean
)