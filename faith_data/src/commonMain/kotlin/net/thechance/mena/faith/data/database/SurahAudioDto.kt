package net.thechance.mena.faith.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "surah_audio",
    primaryKeys = ["surahId", "reciter_id"]
)
data class SurahAudioDto(
    val surahId: Int,
    @ColumnInfo(name = "reciter_id")
    val reciterId: Int,
    @ColumnInfo(name = "local_file_path")
    val localFilePath: String
)