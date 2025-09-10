package net.thechance.mena.faith.data.local

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

class QuranLocalDataSource {
    fun getSur(): List<Surah>{
        TODO()
    }
    fun getSurahById(id: Int): Surah {
        TODO()
    }
    fun getAyatOfSurah(surahId: Int): List<Ayah>{
        TODO()
    }
    fun getAyahContent(ayahNo:Int, surahId: Int): String{
        TODO()
    }
}