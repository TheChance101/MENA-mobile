package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import net.thechance.mena.faith.data.remote.model.tilawah.AyahSoundUrlRequest
import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest
import net.thechance.mena.faith.data.remote.model.tilawah.SurahSoundRequest

interface TilawahApiService {

    @GET("faith/tilawah/ayah/sound")
    suspend fun getAyahSoundUrl(
        @Body body: AyahSoundUrlRequest
    ): Response<String>

    @GET("faith/tilawah/reciters")
    suspend fun getReciters(): Response<List<RecitersRequest>>

    @GET("faith/tilawah/surah/sound")
    suspend fun getSurahSoundUrl(
        @Body body: SurahSoundRequest
    ): Response<String>

}