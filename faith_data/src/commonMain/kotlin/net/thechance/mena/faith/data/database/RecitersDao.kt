package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecitersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReciters(reciters: List<ReciterDto>)

    @Query("SELECT * FROM reciters")
    suspend fun getAllReciters(): List<ReciterDto>

    @Query(
        """
    SELECT * FROM reciters 
    WHERE name LIKE '%'||:query||'%' 
       OR name_ar LIKE '%'||:query||'%'
    ORDER BY name ASC
"""
    )
    suspend fun searchReciters(query: String): List<ReciterDto>

    @Query("SELECT * FROM reciters WHERE id = :reciterId")
    suspend fun getReciterById(reciterId: Int): ReciterDto

    @Query("DELETE FROM surah_audio WHERE surahId = :surahId")
    suspend fun deleteSurahAudioByReciter(surahId: Int)

    @Query("""
    DELETE FROM surah_audio 
    WHERE surahId = :surahId AND reciter_id = :reciterId
""")
    suspend fun deleteSpecificDownloadedAudio(
        surahId: Int,
        reciterId: Int
    )

}
