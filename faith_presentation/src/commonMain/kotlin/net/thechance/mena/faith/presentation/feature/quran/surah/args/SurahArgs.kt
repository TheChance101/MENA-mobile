package net.thechance.mena.faith.presentation.feature.quran.surah.args

interface SurahArgs {
    val surahId: Int
    val ayahNumber: Int?
        get() = null

}