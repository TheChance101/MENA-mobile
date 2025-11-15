package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest

interface TilawahApiService {

    @GET("faith/tilawah/ayah/sound")
    suspend fun getAyahSoundUrl(
        @Query reciterId: Int,
        @Query ayahNumber: Int,
        @Query surahNumber: Int,
    ): Response<String>

    @GET("faith/tilawah/reciters")
    suspend fun getReciters(): Response<List<RecitersRequest>>

    @GET("faith/tilawah/surah/sound")
    suspend fun getSurahSoundUrl(
        @Query reciterId: Int,
        @Query surahNumber: Int,
    ): Response<String>

}