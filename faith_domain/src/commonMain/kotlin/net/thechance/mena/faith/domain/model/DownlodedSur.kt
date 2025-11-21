package net.thechance.mena.faith.domain.model

import net.thechance.mena.faith.domain.entity.Surah

data class DownlodedSur(
    val id: Int,
    val arabicNameImg: Surah.SurahOrder,
    val surahName: String,
    val recitersName: List<String>

)
