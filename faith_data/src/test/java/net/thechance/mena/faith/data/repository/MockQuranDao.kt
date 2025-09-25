package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.QuranDao
import net.thechance.mena.faith.data.database.SurahDto

internal class MockQuranDao : QuranDao {

    private val ayahBySurah: MutableMap<Int, MutableList<AyahDto>> = mutableMapOf(
        1 to mutableListOf(
            AyahDto(
                id = 1,
                surahNumber = 1,
                surahName = "Al-Fatiha",
                surahNameAr = "الفاتحة",
                number = 1,
                displayContent = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيمِ",
                plainTextContent = "بسم الله الرحمن الرحيم",
                lineStart = 1,
                lineEnd = 1,
                jozz = 1,
                page = 1
            ),
            AyahDto(
                id = 2,
                surahNumber = 1,
                surahName = "Al-Fatiha",
                surahNameAr = "الفاتحة",
                number = 2,
                displayContent = "ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ",
                plainTextContent = "الحمد لله رب العالمين",
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
                surahName = "Al-Baqarah",
                surahNameAr = "البقرة",
                number = 5,
                displayContent = "أُوْلَٰٓئِكَ عَلَىٰ هُدٗى مِّن رَّبِّهِمۡۖ وَأُوْلَٰٓئِكَ هُمُ ٱلۡمُفۡلِحُونَ",
                plainTextContent = "أولئك على هدى من ربهم وأولئك هم المفلحون",
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

    override suspend fun getAllSur(): List<SurahDto> {
        return ayahBySurah.entries.map { (surahNo, ayat) ->
            val surahName = ayat.firstOrNull()?.surahName ?: "Surah $surahNo"
            SurahDto(order = surahNo, nameEn = surahName)
        }
    }

    override suspend fun getAyahContent(ayahNumber: Int, surahId: Int): String {
        return getAyah(ayahNumber, surahId).displayContent
    }

    override suspend fun getAyah(ayahId: Int, surahId: Int): AyahDto {
        val direct = ayahBySurah[surahId]?.firstOrNull { it.number == ayahId }
        if (direct != null) return direct

        val swapped = ayahBySurah[ayahId]?.firstOrNull { it.number == surahId }
        if (swapped != null) return swapped

        error("Ayah not found for surahId=$surahId, ayah=$ayahId")
    }

    override suspend fun getSurah(surahId: Int): SurahDto {
        val name = ayahBySurah[surahId]?.firstOrNull()?.surahName ?: "Surah $surahId"
        return SurahDto(order = surahId, nameEn = name)
    }
}
