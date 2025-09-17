package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranRepositoryImpl : QuranRepository {
    override suspend fun getAllSur(): List<Surah> {
        return listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = "Al-Fatiha",
                ayahCount = 7,
                isMakkia = true
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = "Al-Baqara",
                ayahCount = 286,
                isMakkia = false
            ),
            Surah(
                id = 3,
                order = Surah.SurahOrder.AalImran,
                name = "Aal-E-Imran",
                ayahCount = 200,
                isMakkia = false
            ),
            Surah(
                id = 4,
                order = Surah.SurahOrder.AnNisa,
                name = "An-Nisa",
                ayahCount = 176,
                isMakkia = false
            ),
            Surah(
                id = 5,
                order = Surah.SurahOrder.AlMaidah,
                name = "Al-Maidah",
                ayahCount = 120,
                isMakkia = true
            ),
            Surah(
                id = 6,
                order = Surah.SurahOrder.AlAnam,
                name = "Al-An'am",
                ayahCount = 165,
                isMakkia = true
            ),
            Surah(
                id = 7,
                order = Surah.SurahOrder.AlAraf,
                name = "Al-A'raf",
                ayahCount = 206,
                isMakkia = true
            ),
            Surah(
                id = 8,
                order = Surah.SurahOrder.AlAnfal,
                name = "Al-Anfal",
                ayahCount = 75,
                isMakkia = true
            ),
            Surah(
                id = 9,
                order = Surah.SurahOrder.AtTawbah,
                name = "At-Tawbah",
                ayahCount = 129,
                isMakkia = true
            ),
        )
    }

    override suspend fun getAyatOfSurah(id: Int): List<Ayah> {
        return listOf(
            Ayah(
                number = 1,
                surahId = 2,
                content = "‏‏‏ ‏"
            ), Ayah(
                number = 2,
                surahId = 2,
                content = "‏‏‏ ‏‏‏‏‏ ‏ ‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 3,
                surahId = 2,
                content = "‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 4,
                surahId = 2,
                content = "‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏ ‏‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 5,
                surahId = 2,
                content = "‏‏‏‏‏ ‏ ‏‏‏ ‏‏ ‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 6,
                surahId = 2,
                content = "‏‏ ‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏‏‏‏ ‏‏ ‏‏ ‏‏‏‏‏‏ ‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 7,
                surahId = 2,
                content = "‏‏‏ ‏‏ ‏ ‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏"
            ), Ayah(
                number = 8,
                surahId = 2,
                content = "‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 9,
                surahId = 2,
                content = "‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 10,
                surahId = 2,
                content = "‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 11,
                surahId = 2,
                content = "‏‏‏ ‏‏‏ ‏‏‏ ‏ ‏‏‏‏‏‏ ‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 12,
                surahId = 2,
                content = "‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏‏‏ ‏‏‏‏ ‏ ‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 13,
                surahId = 2,
                content = "‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏‏‏ ‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏ ‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 14,
                surahId = 2,
                content = "‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 15,
                surahId = 2,
                content = "‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏ ‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 16,
                surahId = 2,
                content = "‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 17,
                surahId = 2,
                content = "‏‏‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏ ‏‏‏‏ ‏‏‏ ‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏‏‏ ‏ ‏‏‏‏ ‏ ‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 18, surahId = 2, content = "‏‏ ‏‏‏ ‏‏ ‏‏‏ ‏ ‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 19,
                surahId = 2,
                content = "‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏‏ ‏ ‏‏‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏‏‏‏‏ ‏"
            ), Ayah(
                number = 20,
                surahId = 2,
                content = "‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏‏ ‏‏ ‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏‏‏‏‏ ‏‏ ‏‏ ‏ ‏ ‏‏ ‏‏‏‏ ‏"
            ), Ayah(
                number = 21,
                surahId = 2,
                content = "‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏"
            ), Ayah(
                number = 22,
                surahId = 2,
                content = "‏‏‏ ‏‏‏ ‏‏‏ ‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏‏‏ ‏‏‏‏ ‏‏‏‏‏ ‏‏ ‏‏‏‏‏‏ ‏‏‏ ‏‏‏‏‏ ‏‏‏ ‏‏ ‏‏‏‏‏ ‏‏‏‏‏ ‏‏‏‏ ‏‏ ‏‏‏‏‏ ‏ ‏‏‏‏‏‏ ‏‏‏"

            )
        )
    }

}