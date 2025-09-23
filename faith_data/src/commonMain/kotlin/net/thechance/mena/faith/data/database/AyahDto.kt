package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayat")
data class AyahDto(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "sura_no") val surahNumber: Int,
    @ColumnInfo(name = "sura_name_en") val surahName: String,
    @ColumnInfo(name = "sura_name_ar") val surahNameAr: String,
    @ColumnInfo(name = "aya_no") val number: Int,
    @ColumnInfo(name = "aya_text") val displayContent: String,
    @ColumnInfo(name = "aya_text_emlaey") val plainTextContent: String,
    @ColumnInfo(name = "line_start") val lineStart: Int,
    @ColumnInfo(name = "line_end") val lineEnd: Int,
    @ColumnInfo(name = "jozz") val jozz: Int,
    @ColumnInfo(name = "page") val page: Int,
)