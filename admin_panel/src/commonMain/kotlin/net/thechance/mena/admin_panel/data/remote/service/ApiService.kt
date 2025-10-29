package net.thechance.mena.admin_panel.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface ApiService {
    @GET("endpoint")
    suspend fun getSomething(): Response<Any>
}