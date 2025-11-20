package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo

data class SurahDto(
    @ColumnInfo(name = "sura_no")
    val number: Int,
    @ColumnInfo(name = "sura_name_en")
    val nameEn: String,
    @ColumnInfo(name = "sura_name_ar")
    val nameAr: String,
    @ColumnInfo(name = "ayahCount")
    val ayahCount: Int?
)