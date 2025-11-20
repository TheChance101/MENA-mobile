package net.thechance.mena.faith.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
        sura_name_ar,
        CAST(COUNT(*) AS INTEGER) AS ayahCount
    FROM ayat 
    GROUP BY sura_no, sura_name_en
""")
    suspend fun getSur(): List<SurahDto>

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

    @Query(
        """
                SELECT *
                FROM ayat
                WHERE aya_text_emlaey LIKE '%'||:query||'%'
        """
    )
    suspend fun searchForAyahInQuran(query: String): List<AyahDto>

    @Query(
        """
                SELECT *
                FROM ayat
                WHERE aya_text_emlaey LIKE '%'||:query||'%' AND sura_no = :surahId
        """
    )
    suspend fun searchForAyahInSurah(surahId: Int, query: String): List<AyahDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReciters(reciters: List<ReciterDto>)

    @Query("SELECT * FROM reciters")
    suspend fun getAllReciters(): List<ReciterDto>

    @Query("""
    SELECT * FROM reciters 
    WHERE name LIKE '%'||:query||'%' 
       OR name_ar LIKE '%'||:query||'%'
    ORDER BY name ASC
""")
    suspend fun searchReciters(query: String): List<ReciterDto>

    @Query("SELECT * FROM reciters WHERE id = :reciterId")
    suspend fun getReciterById(reciterId: Int): ReciterDto
}