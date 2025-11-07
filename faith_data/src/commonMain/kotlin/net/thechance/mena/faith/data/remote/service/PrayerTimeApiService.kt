package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto

interface PrayerTimeApiService {

    @GET("faith/prayer/times/{date}")
    suspend fun getPrayerTimes(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("isHijri") isHijri: Boolean = false
    ): Response<PrayerTimesDto>
}
