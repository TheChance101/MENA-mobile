package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reciters")
data class ReciterDto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "name_ar")
    val nameAr: String,
    val tilawahType: String
)