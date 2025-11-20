package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SurahAudioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSurahAudio(surahAudio: SurahAudioDto)

    @Query("SELECT local_file_path FROM surah_audio WHERE surahId = :surahId AND reciter_id = :reciterId")
    suspend fun getCachedAudioPath(surahId: Int?, reciterId: Int): String?

    @Query("SELECT * FROM surah_audio")
    suspend fun getDownloadedSurahInfo(): List<SurahAudioDto>

}
