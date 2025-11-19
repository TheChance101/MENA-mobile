package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import net.thechance.mena.admin_panel.data.remote.dto.deposit.CountryDto

interface PublicApiService {
    @GET(GET_COUNTRIES_END_POINT)
    suspend fun getCountries(
        @Header("Accept-Language") language: String = "en"
    ): Response<List<CountryDto>>

    private companion object {
        const val GET_COUNTRIES_END_POINT = "identity/authentication/countries"
    }
}