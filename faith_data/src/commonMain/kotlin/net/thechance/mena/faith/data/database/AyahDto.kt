package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayat")
data class AyahDto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "sura_no")
    val surahNumber: Int,
    @ColumnInfo(name = "sura_name_en")
    val surahNameEn: String,
    @ColumnInfo(name = "sura_name_ar")
    val surahNameAr: String,
    @ColumnInfo(name = "aya_no")
    val number: Int,
    @ColumnInfo(name = "aya_text")
    val content: String,
    @ColumnInfo(name = "aya_text_emlaey")
    val plainContent: String,
    @ColumnInfo(name = "line_start")
    val lineStart: Int,
    @ColumnInfo(name = "line_end")
    val lineEnd: Int,
    @ColumnInfo(name = "jozz")
    val jozz: Int,
    @ColumnInfo(name = "page")
    val page: Int,
)