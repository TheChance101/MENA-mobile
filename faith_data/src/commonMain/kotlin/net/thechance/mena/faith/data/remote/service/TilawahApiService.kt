package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import net.thechance.mena.faith.data.remote.model.tilawah.AyahSoundUrlRequest

interface TilawahApiService {

    @GET("faith/tilawah/ayah/sound")
    suspend fun getAyahSoundUrl(
        @Body body: AyahSoundUrlRequest
    ): Response<String>
}
