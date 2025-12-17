package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.mosque.MosqueDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.request.forms.MultiPartFormDataContent
import net.thechance.mena.faith.data.remote.model.mosque.MosqueResponseDto

interface MosqueApiService {

    @Multipart
    @POST("faith/mosques")
    suspend fun createMosque(
        @Body mosque: MultiPartFormDataContent
    ): Response<MosqueResponseDto>

    @GET("faith/mosques/nearby")
    suspend fun getNearbyMosques(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 10.0
    ): Response<List<MosqueDto>>
    @GET("faith/mosques/search")
    suspend fun searchMosquesByName(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<MosqueDto>>
}