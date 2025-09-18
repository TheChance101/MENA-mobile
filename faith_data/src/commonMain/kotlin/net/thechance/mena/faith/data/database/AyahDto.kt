package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayat")
data class AyahDto(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "sura_no") val surahNumber: Int,
    @ColumnInfo(name = "sura_name_en") val surahName: String,
    @ColumnInfo(name = "aya_no") val number: Int,
    @ColumnInfo(name = "aya_text") val text: String,
)