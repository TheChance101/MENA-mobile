package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.ReciterDto
import net.thechance.mena.faith.data.database.SurahAudioDto
import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.DownlodedSur
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.identity.domain.util.AppLanguage

fun RecitersRequest.toReciter(): Reciter = Reciter(
    id = id,
    name = name,
    tilawahType = tilawahType
)

fun ReciterDto.toReciter(appLanguage: AppLanguage): Reciter = Reciter(
    id = id,
    name = if (appLanguage == AppLanguage.ENGLISH) name else nameAr,
    tilawahType = handleTilawah(appLanguage = appLanguage, tilawahType = tilawahType)
)

fun Reciter.toReciterDto(): ReciterDto = ReciterDto(
    id = id,
    name = name,
    nameAr = name,
    tilawahType = tilawahType
)

fun SurahAudioDto.toDownlodedSurUi(
    surahName: String,
    reciterName: List<String>,
): DownlodedSur = DownlodedSur(
    id = surahId,
    arabicNameImg = Surah.SurahOrder.entries.first { it.order == surahId },
    surahName = surahName,
    recitersName = reciterName
)

private fun handleTilawah(appLanguage: AppLanguage, tilawahType: String): String =
    when (appLanguage) {
        AppLanguage.ENGLISH -> tilawahType
        else -> when (tilawahType) {
            "Mujawwad" -> "مُجود"
            else -> "مُرتل"
        }
    }