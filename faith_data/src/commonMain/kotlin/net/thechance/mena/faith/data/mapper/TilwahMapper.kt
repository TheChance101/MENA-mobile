package net.thechance.mena.faith.data.mapper

import net.thechance.mena.faith.data.database.ReciterDto
import net.thechance.mena.faith.data.database.SurahAudioDto
import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.DownlodedSur
import net.thechance.mena.faith.domain.model.Reciter

fun RecitersRequest.toReciter(): Reciter = Reciter(
    id = id,
    name = name,
    arabicName = arabicName,
    tilawahType = tilawahType
)

fun ReciterDto.toReciter(): Reciter = Reciter(
    id = id,
    name = name,
    arabicName = nameAr,
    tilawahType = tilawahType
)

fun Reciter.toReciterDto(): ReciterDto = ReciterDto(
        id = id,
        name = name,
        nameAr = arabicName,
        tilawahType = tilawahType
    )

fun SurahAudioDto.toDownlodedSurUi(
    surahName: String,
    reciterName: List<String>
): DownlodedSur = DownlodedSur(
    id = surahId,
    arabicNameImg = Surah.SurahOrder.entries.first { it.order == surahId },
    surahName = surahName,
    recitersName = reciterName
)