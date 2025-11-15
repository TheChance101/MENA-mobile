package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.ReciterDto
import net.thechance.mena.faith.data.database.SurahDto

internal class MockAyahDao : AyahDao {

    private val reciters: MutableList<ReciterDto> = mutableListOf(
        ReciterDto(
            id = 1,
            name = "Abd elbasit",
            nameAr = "عبدالباسط",
            tilawahType = "Mujawad"
        )
    )

    private val ayahBySurah: MutableMap<Int, MutableList<AyahDto>> = mutableMapOf(
        1 to mutableListOf(
            AyahDto(
                id = 1,
                surahNumber = 1,
                surahNameEn = "Al-Fatiha",
                surahNameAr = "الفاتحة",
                number = 1,
                content = "Ayah content",
                plainContent = "Ayah plain content",
                lineStart = 1,
                lineEnd = 1,
                jozz = 1,
                page = 1
            ),
            AyahDto(
                id = 2,
                surahNumber = 1,
                surahNameEn = "Al-Fatiha",
                surahNameAr = "الفاتحة",
                number = 2,
                content = "Ayah content",
                plainContent = "Ayah plain content",
                lineStart = 1,
                lineEnd = 1,
                jozz = 1,
                page = 1
            ),
        ),
        2 to mutableListOf(
            AyahDto(
                id = 3,
                surahNumber = 2,
                surahNameEn = "Al-Baqarah",
                surahNameAr = "البقرة",
                number = 5,
                content = "Ayah content",
                plainContent = "Ayah plain content",
                lineStart = 2,
                lineEnd = 2,
                jozz = 1,
                page = 2
            )
        )
    )

    override suspend fun getAyatOfSurah(surahNumber: Int): List<AyahDto> {
        return ayahBySurah[surahNumber]?.toList() ?: emptyList()
    }

    override suspend fun getSur(): List<SurahDto> {
        return ayahBySurah.entries.map { (surahNo, ayat) ->
            val surahNameEn = ayat.firstOrNull()?.surahNameEn ?: "Surah $surahNo"
            val surahNameAr = ayat.firstOrNull()?.surahNameAr ?: "Surah $surahNo"
            SurahDto(
                number = surahNo,
                nameEn = surahNameEn,
                nameAr = surahNameAr,
                ayahCount = ayat.size
            )
        }
    }

    override suspend fun getAyahContent(ayahNumber: Int, surahId: Int): String {
        return getAyah(ayahNumber, surahId).content
    }

    override suspend fun searchForAyahInQuran(query: String): List<AyahDto> =
        ayahBySurah.values.flatten().filter {
            it.plainContent.contains(query)
        }

    override suspend fun searchForAyahInSurah(
        surahId: Int,
        query: String,
    ): List<AyahDto> =
        ayahBySurah[surahId]?.filter {
            it.plainContent.contains(query)
        } ?: emptyList()

    override suspend fun insertReciters(reciters: List<ReciterDto>) {
        this.reciters.clear()
        this.reciters.addAll(reciters)
    }

    override suspend fun getAllReciters(): List<ReciterDto> {
        return reciters.toList()
    }

    override suspend fun searchReciters(query: String): List<ReciterDto> {
        return reciters.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.nameAr.contains(query) ||
                    it.tilawahType.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getReciterById(reciterId: Int): ReciterDto {
        return reciters.find { it.id == reciterId }
            ?: error("Reciter not found with id=$reciterId")
    }

    override suspend fun getAyah(ayahId: Int, surahId: Int): AyahDto {
        val direct = ayahBySurah[surahId]?.firstOrNull { it.number == ayahId }
        if (direct != null) return direct

        val swapped = ayahBySurah[ayahId]?.firstOrNull { it.number == surahId }
        if (swapped != null) return swapped

        error("Ayah not found for surahId=$surahId, ayah=$ayahId")
    }

    override suspend fun getSurah(surahId: Int): SurahDto {
        val nameEn = ayahBySurah[surahId]?.firstOrNull()?.surahNameEn ?: "Surah $surahId"
        val nameAr = ayahBySurah[surahId]?.firstOrNull()?.surahNameAr ?: "Surah $surahId"
        return SurahDto(
            number = surahId,
            nameEn = nameEn,
            nameAr = nameAr,
            ayahCount = ayahBySurah[surahId]?.size ?: 0
        )
    }
}