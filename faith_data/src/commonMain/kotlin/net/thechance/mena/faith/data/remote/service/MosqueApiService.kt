package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto

interface MosqueApiService {

    @GET("faith/mosques/nearby")
    suspend fun getNearbyMosques(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 10.0
    ): Response<List<MosqueDto>>
}