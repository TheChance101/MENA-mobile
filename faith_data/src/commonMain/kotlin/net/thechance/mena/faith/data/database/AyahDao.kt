package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AyahDao {
    @Query("""
    SELECT aya_no, aya_text ,aya_text_emlaey
    FROM ayat 
    WHERE sura_no = :surahNumber
""")
    suspend fun getAyatOfSurah(surahNumber: Int): List<SimpleAyahDto>


    @Query("""SELECT 
        sura_no,
        sura_name_en,
        COUNT(*) as ayahCount
    FROM ayat 
    GROUP BY sura_no, sura_name_en
""")
    suspend fun getAllSur(): List<SurahDto>

    @Query("""
        SELECT aya_text 
        FROM ayat 
        WHERE sura_no = :surahId AND aya_no = :ayahNumber
        LIMIT 1
    """)
    suspend fun getAyahContent(ayahNumber:Int, surahId: Int): String
}