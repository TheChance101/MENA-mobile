package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AyaDao {
    @Query("SELECT * FROM ayat WHERE sura_no = :surahNumber")
    suspend fun getAyatOfSura(surahNumber: Int): List<AyahDto>

    @Query(
        """
        SELECT 
            sura_no AS id,
            sura_name_en AS name,
            COUNT(*) AS ayatCount,
            CASE 
            WHEN sura_no IN (2, 3, 4, 5, 8, 9, 13, 22, 24, 33, 47, 48, 49, 55, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 76, 98, 99, 110) 
            THEN 0 
            ELSE 1 
        END AS isMakki
        FROM ayat
        GROUP BY sura_no, sura_name_en
        ORDER BY sura_no
        """
    )
    suspend fun getAllSur(): List<SurahDto>
}