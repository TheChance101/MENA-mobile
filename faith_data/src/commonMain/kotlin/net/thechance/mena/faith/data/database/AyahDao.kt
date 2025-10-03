package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
@RewriteQueriesToDropUnusedColumns
interface AyahDao {
    @Query("""
    SELECT *
    FROM ayat 
    WHERE sura_no = :surahNumber
""")
    suspend fun getAyatOfSurah(surahNumber: Int): List<AyahDto>

    @Query(
        """
    SELECT 
        sura_no,
        sura_name_en,
        CAST(COUNT(*) AS INTEGER) AS ayahCount
    FROM ayat 
    GROUP BY sura_no, sura_name_en
""")
    suspend fun getAllSur(): List<SurahDto>


    @Query("SELECT * FROM ayat WHERE aya_no = :ayahId AND sura_no = :surahId")
    suspend fun getAyah(ayahId: Int, surahId: Int): AyahDto

    @Query("SELECT * FROM ayat WHERE sura_no = :surahId")
    suspend fun getSurah(surahId: Int): SurahDto

    @Query(
        """
        SELECT aya_text 
        FROM ayat 
        WHERE sura_no = :surahId AND aya_no = :ayahNumber
        LIMIT 1
    """
    )
    suspend fun getAyahContent(ayahNumber: Int, surahId: Int): String
}